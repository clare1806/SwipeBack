package info.papdt.swipeback.ui.app;

import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

import java.util.HashSet;
import java.util.Set;

import info.papdt.swipeback.R;
import info.papdt.swipeback.helper.Settings;
import info.papdt.swipeback.ui.base.BasePreferenceFragment;
import static info.papdt.swipeback.ui.utils.UiUtility.*;

public class SettingsFragment extends BasePreferenceFragment
{
	private static final String EDGE_LEFT = "0",
								EDGE_RIGHT = "1",
								EDGE_BOTTOM = "2";
	
	private Settings mSettings;
	
	private SwitchPreference mEnable;
	private MultiSelectListPreference mEdge;
	
	private String mPackageName, mClassName;

	@Override
	protected int getPreferenceXml() {
		return R.xml.pref;
	}

	@Override
	protected void onPreferenceLoaded() {
		initPackage();
		mSettings = Settings.getInstance(getActivity());
		
		// Obtain preferences
		mEnable = $(this, Settings.ENABLE);
		mEdge = $(this, Settings.EDGE);
		
		// Default values
		mEnable.setChecked(getBoolean(Settings.ENABLE, true));
		Set<String> edges = parseEdgePref(getInt(Settings.EDGE, SwipeBackLayout.EDGE_LEFT));
		mEdge.setValues(edges);
		mEdge.setSummary(buildEdgeText(edges));
		
		// Bind
		$$(mEnable, mEdge);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mEnable) {
			putBoolean(Settings.ENABLE, Boolean.valueOf(newValue));
			return true;
		} else if (preference == mEdge) {
			Set<String> values = (Set<String>) newValue;
			putInt(Settings.EDGE, buildEdgePref(values));
			mEdge.setSummary(buildEdgeText(values));
			return true;
		} else {
			return false;
		}
	}
	
	private Set<String> parseEdgePref(int pref) {
		HashSet<String> ret = new HashSet<String>();
		if ((pref & SwipeBackLayout.EDGE_LEFT) != 0) {
			ret.add(EDGE_LEFT);
		}
		
		if ((pref & SwipeBackLayout.EDGE_RIGHT) != 0) {
			ret.add(EDGE_RIGHT);
		}
		
		if ((pref & SwipeBackLayout.EDGE_BOTTOM) != 0) {
			ret.add(EDGE_BOTTOM);
		}
		
		return ret;
	}
	
	private String buildEdgeText(Set<String> edges) {
		StringBuilder sb = new StringBuilder();
		CharSequence[] entries = mEdge.getEntries();
		for (String edge : edges) {
			sb.append(entries[Integer.parseInt(edge)]).append(", ");
		}
		
		return sb.toString().substring(0, sb.length() - 2);
	}
	
	private int buildEdgePref(Set<String> values) {
		int pref = 0;
		
		for (String value : values) {
			switch (value) {
				case EDGE_LEFT:
					pref |= SwipeBackLayout.EDGE_LEFT;
					break;
				case EDGE_RIGHT:
					pref |= SwipeBackLayout.EDGE_RIGHT;
					break;
				case EDGE_BOTTOM:
					pref |= SwipeBackLayout.EDGE_BOTTOM;
					break;
			}
		}
		
		return pref;
	}
	
	private void initPackage() {
		String[] str = getExtraPass().split(",");
		mPackageName = str[0];
		mClassName = str[1];
		
		if (str[2].startsWith("Global")) {
			str[2] = getString(R.string.global_short);
		}
		
		setTitle(str[2] + " - " + str[3]);
		showHomeAsUp();
	}
	
	private boolean getBoolean(String key, boolean defValue) {
		return mSettings.getBoolean(mPackageName, mClassName, key, defValue);
	}
	
	private int getInt(String key, int defValue) {
		return mSettings.getInt(mPackageName, mClassName, key, defValue);
	}
	
	private void putBoolean(String key, boolean value) {
		mSettings.putBoolean(mPackageName, mClassName, key, value);
	}
	
	private void putInt(String key, int value) {
		mSettings.putInt(mPackageName, mClassName, key, value);
	}
}