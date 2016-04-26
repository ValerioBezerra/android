package br.com.kingsoft.procureaki.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	
	public static boolean validarEmail(String email) {
        boolean isEmailIdValid = false;  
        if (email != null && email.length() > 0) {  
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";  
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);  
            Matcher matcher = pattern.matcher(email);  
            if (matcher.matches()) {  
                isEmailIdValid = true;  
            }  
        }  
        return isEmailIdValid;  
    }  
	
	public static void gerarToastLongo(Context ctx, String mensagem) {
		Toast.makeText(ctx, mensagem, Toast.LENGTH_LONG).show();		
	}

	public static void gerarToastCurto(Context ctx, String mensagem) {
		Toast.makeText(ctx, mensagem, Toast.LENGTH_SHORT).show();		
	}
	
	public static void messagem(String messagem, Handler handler) {
		Message m = handler.obtainMessage();
	    m.obj = messagem;
	    handler.sendMessage(m); 
	}
	
	public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("br.com.encontredelivery", PackageManager.GET_SIGNATURES); 
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }
	
	public static String getHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("br.com.encontredelivery", PackageManager.GET_SIGNATURES); 
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
            
            return "";
        } catch (NameNotFoundException e) {
        	return null;
        } catch (NoSuchAlgorithmException e) {
        	return null;
        }
    }
	

}
