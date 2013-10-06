package net.alf.osgap.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class WinRegistryLight {
	public static final int HKEY_CURRENT_USER = 0x80000001;
	public static final int HKEY_LOCAL_MACHINE = 0x80000002;
	public static final int REG_SUCCESS = 0;
	public static final int REG_NOTFOUND = 2;
	public static final int REG_ACCESSDENIED = 5;

	private static final int KEY_READ = 0x20019;
	private static Preferences userRoot;
	private static Preferences systemRoot;
	private static Class<? extends Preferences> userClass;
	private static Method regOpenKey = null;
	private static Method regCloseKey = null;
	private static Method regQueryValueEx = null;
	private static Method regEnumValue = null;
	private static Method regQueryInfoKey = null;
	private static Method regEnumKeyEx = null;

	static {
		try {
			userRoot = Preferences.userRoot();
			systemRoot = Preferences.systemRoot();
			userClass = userRoot.getClass();
			regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey", new Class[] { int.class, byte[].class, int.class });
			regOpenKey.setAccessible(true);
			regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", new Class[] { int.class });
			regCloseKey.setAccessible(true);
			regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx", new Class[] { int.class, byte[].class });
			regQueryValueEx.setAccessible(true);
			regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue", new Class[] { int.class, int.class, int.class });
			regEnumValue.setAccessible(true);
			regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", new Class[] { int.class });
			regQueryInfoKey.setAccessible(true);
			regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx", new Class[] { int.class, int.class, int.class });
			regEnumKeyEx.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private WinRegistryLight() {
	}

	/**
	 * Read a value from key and value name
	 * 
	 * @param hkey
	 *            HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @param valueName
	 * @return the value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String readString(int hkey, String key, String valueName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readString(systemRoot, hkey, key, valueName);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readString(userRoot, hkey, key, valueName);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**
	 * Read value(s) and value name(s) form given key
	 * 
	 * @param hkey
	 *            HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @return the value name(s) plus the value(s)
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Map<String, String> readStringValues(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readStringValues(systemRoot, hkey, key);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readStringValues(userRoot, hkey, key);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**
	 * Read the value name(s) from a given key
	 * 
	 * @param hkey
	 *            HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @return the value name(s)
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<String> readStringSubKeys(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readStringSubKeys(systemRoot, hkey, key);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readStringSubKeys(userRoot, hkey, key);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	// =====================

	private static String readString(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] { new Integer(hkey), toCstr(key), new Integer(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[] { new Integer(handles[0]), toCstr(value) });
		regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
		return (valb != null ? new String(valb).trim() : null);
	}

	private static Map<String, String> readStringValues(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		HashMap<String, String> results = new HashMap<String, String>();
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] { new Integer(hkey), toCstr(key), new Integer(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { new Integer(handles[0]) });

		int count = info[0]; // count
		int maxlen = info[3]; // value length max
		for (int index = 0; index < count; index++) {
			byte[] name = (byte[]) regEnumValue.invoke(root, new Object[] { new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1) });
			String value = readString(hkey, key, new String(name));
			results.put(new String(name).trim(), value);
		}
		regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
		return results;
	}

	private static List<String> readStringSubKeys(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		List<String> results = new ArrayList<String>();
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] { new Integer(hkey), toCstr(key), new Integer(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { new Integer(handles[0]) });

		int count = info[0]; // Fix: info[2] was being used here with wrong
								// results. Suggested by davenpcj, confirmed by
								// Petrucio
		int maxlen = info[3]; // value length max
		for (int index = 0; index < count; index++) {
			byte[] name = (byte[]) regEnumKeyEx.invoke(root, new Object[] { new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1) });
			results.add(new String(name).trim());
		}
		regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
		return results;
	}

	// utility
	private static byte[] toCstr(String str) {
		byte[] result = new byte[str.length() + 1];

		for (int i = 0; i < str.length(); i++) {
			result[i] = (byte) str.charAt(i);
		}
		result[str.length()] = 0;
		return result;
	}
}