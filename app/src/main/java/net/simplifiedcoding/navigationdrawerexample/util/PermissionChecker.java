package net.simplifiedcoding.navigationdrawerexample.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by vibes on 8/2/16.
 */
public class PermissionChecker {

    public static boolean lacksPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lacksPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }

}
