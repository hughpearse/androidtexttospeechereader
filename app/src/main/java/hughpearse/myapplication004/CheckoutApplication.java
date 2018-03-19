package hughpearse.myapplication004;

import org.solovyev.android.checkout.Billing;
import android.app.Application;
import javax.annotation.Nonnull;

public class CheckoutApplication extends Application {
    private static CheckoutApplication sInstance;

    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
        @Nonnull
        @Override
        public String getPublicKey() {
            return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzmEIzdyJVjLuygbaFgPxjqDqa08kBX4BK/dsi5w0wobke89St8mrDEHnyKopfFuMI1qrr/kWuBmmVm2mZYoGWDRS7610QTFZuTExuq1QWGj4OQ+ulIL3QvmMHbSoz5zxcOrUcY/oI2yet8ns2iK6MB2Dt55bRdtX8TfpW2Txjc3DNz6hm2gm/5SjqL81h1Kw3VtL8KeliF4BGb5vEd1BIZCfs0Z4+XU9wIFBxMWt+EH3OyV0mzrRYeCmZko/8Po+dSvTw8GZoAlzTl3yxm0MBtuMYm0DCT+o/0GOIVwurFHCKm2DQ5ECfNRC7kGEGcmP+9ZYP61HKEsDA1uF3y4fRQIDAQAB";
        }
    });

    public CheckoutApplication() {
        sInstance = this;
    }

    public static CheckoutApplication get() {
        return sInstance;
    }

    public Billing getBilling() {
        return mBilling;
    }
}
