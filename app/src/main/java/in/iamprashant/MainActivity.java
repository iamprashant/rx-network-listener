package in.iamprashant;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.IOException;

import in.iamprashant.listener.RxNetworkReceiver;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private  Subscription subs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_layout);

        subs = RxNetworkReceiver.stream(getApplicationContext())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make( mCoordinatorLayout, "Network is not active switch on wifi or mobile data", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).setDuration(Snackbar.LENGTH_INDEFINITE).show();
                    }

                    @Override
                    public void onNext(final Boolean aBoolean) {
                        Log.d("ghjgjhg", "onNext: hfghgfgfhhg");
                        ProcessBuilder pinging = new ProcessBuilder();
                        if (aBoolean) {
                            try {
                                // ping to ip for check internet
                                Observable.just(pinging.command("/system/bin/ping", "-c1", "8.8.8.8")
                                        .redirectErrorStream(true).start())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Process>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Snackbar.make( mCoordinatorLayout, "Network is active but no internet", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).setDuration(Snackbar.LENGTH_INDEFINITE).show();
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onNext(Process process) {
                                                Log.d("check internet", "onNext: "+process.toString());
                                                Snackbar.make( mCoordinatorLayout, "Internet connection is active", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).setDuration(Snackbar.LENGTH_INDEFINITE).show();
                                            }
                                        });
                            } catch (IOException e) {
                                Snackbar.make( mCoordinatorLayout, "Network is active but internet is not working", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).setDuration(Snackbar.LENGTH_INDEFINITE).show();
                                e.printStackTrace();
                            }

                        } else {
                            Snackbar.make( mCoordinatorLayout, "Network is active but internet is not working", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).setDuration(Snackbar.LENGTH_INDEFINITE).show();
                        }
                    }
                });
    }


}
