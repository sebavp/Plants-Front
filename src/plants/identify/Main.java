package plants.identify;

import plants.identify.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class Main extends TabActivity {

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TabHost mTabHost = getTabHost();
        Resources res = getResources();
        Intent intent;
        TabHost.TabSpec spec;
        intent = new Intent().setClass(this, Record.class);
        spec = mTabHost.newTabSpec("lista").setIndicator("Identify",
                res.getDrawable(R.drawable.leaf_icon))
            .setContent(intent);
        mTabHost.addTab(spec);
        
        intent = new Intent().setClass(this, List.class);
        spec = mTabHost.newTabSpec("lista").setIndicator("Lista",
                          res.getDrawable(R.drawable.lista))
                      .setContent(intent);
        mTabHost.addTab(spec);
        intent = new Intent().setClass(this, Config.class);
        spec = mTabHost.newTabSpec("config").setIndicator("Config",
                res.getDrawable(R.drawable.lista))
            .setContent(intent);
        mTabHost.addTab(spec);
        
        mTabHost.setCurrentTab(0);
	}
}
