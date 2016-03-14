package com.mycelium.wallet.glidera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wallet.activity.modern.ModernMain;
import com.mycelium.wallet.glidera.api.GlideraService;
import com.mycelium.wallet.glidera.api.response.GlideraError;
import com.mycelium.wallet.glidera.api.response.StatusResponse;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class BuySellSelect extends FragmentActivity {
    private GlideraService glideraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glidera_buy_sell);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        this.glideraService = GlideraService.getInstance();

        final LinearLayout glideraRow = (LinearLayout) findViewById(R.id.glideraBuySell);

        glideraRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                glideraService.status()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<StatusResponse>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                GlideraError error = GlideraService.convertRetrofitException(e);
                                if (error != null && error.getCode() != null) {
                                    if (error.getCode() == 1103) {
                                        //Invalid credentials, send to bitid registration
                                        String uri = glideraService.getBitidRegistrationUrl();
                                        Utils.openWebsite(BuySellSelect.this, uri);
                                    }
                                }
                            }

                            @Override
                            public void onNext(StatusResponse statusResponse) {
                                if (statusResponse.isUserCanTransact()) {
                                    Intent intent = new Intent(BuySellSelect.this, GlideraMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    //Send to setup
                                    String uri = glideraService.getSetupUrl();
                                    Utils.openWebsite(BuySellSelect.this, uri);
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ModernMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
