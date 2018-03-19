package hughpearse.myapplication004;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Inventory.Product;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;

import static org.solovyev.android.checkout.ResponseCodes.ITEM_ALREADY_OWNED;


public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "TTS-DonateActivity";
    private final ActivityCheckout mCheckout = Checkout.forActivity(this, CheckoutApplication.get().getBilling());
    private Inventory storeInventory;
    private Inventory custInventory;

    //listener for store inventory
    private class StoreInventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(@NonNull Inventory.Products products) {
            Log.i(TAG, "Fetching store prices");
            Button donateButton1 = (Button) findViewById(R.id.donateButton1);
            Button donateButton5 = (Button) findViewById(R.id.donateButton5);
            Button donateButton10 = (Button) findViewById(R.id.donateButton10);
            Button donateButton15 = (Button) findViewById(R.id.donateButton15);

            for (Product p : products) {
                for (Sku sku : p.getSkus()) {
                    if (sku.id.code.contains("donate_1_euro")) {
                        donateButton1.setText("Donate " + sku.price);
                    } else if (sku.id.code.contains("donate_5_euro")) {
                        donateButton5.setText("Donate " + sku.price);
                    } else if (sku.id.code.contains("donate_10_euro")) {
                        donateButton10.setText("Donate " + sku.price);
                    } else if (sku.id.code.contains("donate_15_euro")) {
                        donateButton15.setText("Donate " + sku.price);
                    }
                }
            }
        }
    }

    //listener for customer's inventory
    private class CustomerInventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(@NonNull Inventory.Products products) {
            Log.i(TAG, "Reloading customers inventory");
            //Products [inapp, subs]
            //Skus [donate_10_euro, donate_5_euro]
            for(Product product : products){
                List<Purchase> purchases = product.getPurchases();
                for(Purchase purchase : purchases){
                    try {
                        final JSONObject json = new JSONObject(purchase.data);
                        Log.i(TAG, "Consuming purchase: " + purchase.data);
                        if(json.getString("productId").contains("donate_")){
                            consume(purchase);
                        }
                    } catch (JSONException e){
                        Log.i(TAG, "Failed to parse productId from JSON: " + purchase.data);
                        Log.i(TAG, "Exception: " + e);
                    }
                }
            }
        }
    }

    //listener for purchases
    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        @Override
        public void onSuccess(@NonNull Purchase purchase) {
            //all purchased items are consumed by default
            Log.i(TAG, "Purchase of " + purchase.sku + " completed");
            consume(purchase);
        }

        @Override
        public void onError(int response, @NonNull Exception e) {
            Log.i(TAG, "Purchase failed.");
            Log.i(TAG, "Response " + response);
            Log.i(TAG, "Exception " + e);
            if (response == ITEM_ALREADY_OWNED) {
            }
        }
    }

    //listener for consumed items
    private class ConsumeListener extends EmptyRequestListener<Object> {
        @Override
        public void onSuccess(@Nonnull Object result) {
            Log.i(TAG, "Purchase consumed: ");
            custInventory.load(
                    Inventory.Request.create()
                            .loadAllPurchases(), new CustomerInventoryCallback());
        }

        @Override
        public void onError(int response, @Nonnull Exception e) {
            Log.i(TAG, "Consume failed: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        mCheckout.start();
        mCheckout.createPurchaseFlow(new PurchaseListener());
        storeInventory = mCheckout.makeInventory();
        custInventory = mCheckout.makeInventory();
        storeInventory.load(
                Inventory.Request.create()
                .loadSkus(ProductTypes.IN_APP, getInAppSkus()), new StoreInventoryCallback());
        custInventory.load(
                Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.IN_APP, getInAppSkus()), new CustomerInventoryCallback());

        Button donateButton1 = (Button) findViewById(R.id.donateButton1);
        Button donateButton5 = (Button) findViewById(R.id.donateButton5);
        Button donateButton10 = (Button) findViewById(R.id.donateButton10);
        Button donateButton15 = (Button) findViewById(R.id.donateButton15);

        donateButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(@NonNull BillingRequests requests) {
                        Log.i(TAG, "Button1 was pressed, mCheckout is ready");
                        requests.purchase(ProductTypes.IN_APP, "donate_1_euro", null, mCheckout.getPurchaseFlow()) ;
                    }
                });
            }
        });

        donateButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(@NonNull BillingRequests requests) {
                        Log.i(TAG, "Button5 was pressed, mCheckout is ready");
                        requests.purchase(ProductTypes.IN_APP, "donate_5_euro", null, mCheckout.getPurchaseFlow());
                    }
                });
            }
        });

        donateButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(@NonNull BillingRequests requests) {
                        Log.i(TAG, "Button10 was pressed, mCheckout is ready");
                        requests.purchase(ProductTypes.IN_APP, "donate_10_euro", null, mCheckout.getPurchaseFlow());
                    }
                });
            }
        });

        donateButton15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(@NonNull BillingRequests requests) {
                        Log.i(TAG, "Button15 was pressed, mCheckout is ready");
                        requests.purchase(ProductTypes.IN_APP, "donate_15_euro", null, mCheckout.getPurchaseFlow());
                    }
                });
            }
        });
    }

    private static List<String> getInAppSkus() {
        final List<String> skus = new ArrayList<>();
        skus.addAll(Arrays.asList("donate_1_euro", "donate_5_euro", "donate_10_euro", "donate_15_euro"));
        return skus;
    }

    private void consume(final Purchase purchase) {
        mCheckout.whenReady(new Checkout.EmptyListener() {
            @Override
            public void onReady(@Nonnull BillingRequests requests) {
                requests.consume(purchase.token, new ConsumeListener());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCheckout.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }
}
