package in.iamprashant.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by prashant on 8/9/16.
 */
public class RxNetworkReceiver {
    public static Observable<Boolean> stream(final Context context){
        final IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        final Observable<Boolean> stateStream =
                Observable.create(new OnSubscribeBroadcastRegister(context, intentFilter, null, null))
                        .map(new Func1<Intent, Boolean>() {
                            @Override
                            public  Boolean call(Intent intent){
                                return checkConnectivityStatus(context.getApplicationContext());
                            }
                        });
        return stateStream.startWith(checkConnectivityStatus(context)).distinctUntilChanged();
    }

    public static boolean checkConnectivityStatus(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return null!= networkInfo && networkInfo.isConnected();
    }

    private static class OnSubscribeBroadcastRegister implements Observable.OnSubscribe<Intent> {

        private final Context context;
        private final IntentFilter intentFilter;
        private final String permission;
        private final Handler schedulerHandler;

        public OnSubscribeBroadcastRegister(Context context, IntentFilter intentFilter,String permission, Handler schedulerHandler) {
            this.context = context;
            this.intentFilter = intentFilter;
            this.permission = permission;
            this.schedulerHandler = schedulerHandler;
        }

        @Override
        public void call(final Subscriber<? super Intent> subscriber) {
            final BroadcastReceiver broadCastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    subscriber.onNext(intent);
                }
            };
            final Subscription subscription= Subscriptions.create(new Action0() {
                @Override
                public void call() {
                    context.unregisterReceiver(broadCastReceiver);

                }
            });
            subscriber.add(subscription);
            context.registerReceiver(broadCastReceiver, intentFilter, permission, schedulerHandler);
        }
    }
}
