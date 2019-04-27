package com.example.hyunju.notification_collector.telegram;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PrefsHelper {
	private static final PrefsHelper instance = new PrefsHelper();
	Context mContext;
	
	public static PrefsHelper getInstance() {
		return instance;
	}

	public void init(Context mContext){
		this.mContext = mContext;
	}
	
	private Context getContext(){
		return mContext;
	}
	
	private static SharedPreferences pref (){
		return PreferenceManager.getDefaultSharedPreferences(getInstance().getContext());
	}
	
	public static void clearSettings(){
		Editor editor = pref().edit();
		editor.clear();
		editor.commit();
	}
	
	public static void removeSetting(String param){
		Editor editor = pref().edit();
		editor.remove(param);
		editor.commit();
	}

	public static Object getSetting(String param, Object defVal){

		try{		
		if (defVal instanceof String)
			return pref().getString(param,(String) defVal);
		else if (defVal instanceof Integer)
			return pref().getInt(param,(Integer) defVal);
		else if (defVal instanceof Boolean)
			return pref().getBoolean(param,(Boolean) defVal);
		else if (defVal instanceof Long)
			return pref().getLong(param,(Long) defVal);
		else if (defVal instanceof Float)
			return pref().getFloat(param,(Float) defVal);
		else
			return defVal;
		}catch(Exception e){
			return defVal;
		}
	}
	
public static String getString(String param, String defVal){

		try{
			return pref().getString(param, defVal);
		}catch(Exception e){
			return defVal;
		}
	}
	
	
public static Boolean getBoolean(String param, Boolean defval){
		try{
			return pref().getBoolean(param, defval);
		}catch(Exception e){
			return  defval;
		}
	}
	
	public static void set(String param, Object value){
		Editor editor = pref().edit();
		if (value instanceof String)
			editor.putString(param, (String) value);
		else if (value instanceof Boolean)
			editor.putBoolean(param, (Boolean) value);
		else if (value instanceof Integer)
			editor.putInt(param, (Integer) value);
		else if (value instanceof Long)
			editor.putLong(param, (Long) value);
		else if (value instanceof Float)
			editor.putFloat(param, (Float) value);

		editor.apply();
	}
		
	public static int getInteger(String param, int defval) {
		try{
			return pref().getInt(param, defval);
		}catch(Exception e){
			return defval;
		}			

	}
	
	public static long getLong(String param, long defval) {
		try{
			return pref().getLong(param, defval);
		}catch(Exception e){
			return defval;
		}

	}

	public static float getFloat(String param, float defVal, Context context) {
		try{
			return pref().getFloat(param, defVal);
		}catch(Exception e){
			return defVal;
		}
	}

	public static boolean has(final String param){
		return pref().contains(param);
	}


    public boolean isNull() {
        return mContext==null;
    }

}
