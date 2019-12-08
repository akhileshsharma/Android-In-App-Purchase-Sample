package swapnil.inappsample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private static final String TAG = "MainActivity";
    private BillingClient billingClient;
    private Button btn;
    private TextView tvSkuDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        tvSkuDetails = findViewById(R.id.tv_sku_details);
        
        setUpBillingClient();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (billingClient.isReady()) {
                    Toast.makeText(MainActivity.this, "Billing client is ready", Toast.LENGTH_SHORT).show();
                    SkuDetailsParams params = SkuDetailsParams.newBuilder()
                            .setSkusList(Collections.singletonList("test_product_1"))
                            .setType(BillingClient.SkuType.INAPP)
                            .build();

                    billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                            if (responseCode == BillingClient.BillingResponse.OK) {
                                // TODO: 8/12/19 add this product details inside textview
                                tvSkuDetails.setText("Sku Details: " + skuDetailsList.get(0).getPrice());
                            } else {
                                tvSkuDetails.setText("Sku Details: Cannot query products details " + responseCode);
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Billing client is not ready", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setUpBillingClient() {
        billingClient = BillingClient.newBuilder(this).setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    Toast.makeText(MainActivity.this, "Success to connect billing", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + responseCode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(MainActivity.this, "Billing client disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        Toast.makeText(this, "Purchased items: " + purchases.size(), Toast.LENGTH_SHORT).show();
    }
}
