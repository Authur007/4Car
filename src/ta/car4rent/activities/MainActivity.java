package ta.car4rent.activities;

import java.util.ArrayList;

import ta.car4rent.R;
import ta.car4rent.adapters.NavigationDrawerAdapter;
import ta.car4rent.adapters.NavigationDrawerListItem;
import ta.car4rent.adapters.OnNavigationDrawerItemClickHandler;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.fragments.AccountFragmemt;
import ta.car4rent.fragments.ContactFragmemt;
import ta.car4rent.fragments.IntroduceFragmemt;
import ta.car4rent.fragments.LoginFragmemt;
import ta.car4rent.fragments.PostNewFeedFragmemt;
import ta.car4rent.fragments.SearchCarFragmemt;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * @author anhle
 * 
 */
public class MainActivity extends ActionBarActivity implements OnItemClickListener {
	
	private ArrayList<NavigationDrawerListItem> mNavigationDrawerListItems;
	private NavigationDrawerAdapter mNavigationDrawerAdapter;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String mTitle;
	
	
	// For listen to Open & Close NavigationDrawer menu
	private ActionBarDrawerToggle mDrawerToggle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ConfigureData.activityMain = this;
		
		// set activity for all fragment reference
		ConfigureData.activityMain = this;
		/**
		 * 1. Initial for NavigationDrawer		 
		 */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        mNavigationDrawerListItems = new ArrayList<NavigationDrawerListItem>();

        // Create & Add Items CATEGORY "label_application_session"
        mNavigationDrawerListItems.add(new NavigationDrawerListItem(
        		NavigationDrawerListItem.CATEGORY_NAME,
        		0,
        		this.getString(R.string.label_application_session),
        		"",
        		null
        		));
        
        // Create & Add Items "label_search_car"
        mNavigationDrawerListItems.add(new NavigationDrawerListItem(
        		NavigationDrawerListItem.SCREEN_IN_CATEGORY,
        		R.drawable.ic_nd_search_car,
        		this.getString(R.string.label_search_car),
        		"",
        		new OnNavigationDrawerItemClickHandler() {
					
					@Override
					public void doAction() {
						// Create a new fragment and specify the planet to show based on position
					    Fragment fragment = new SearchCarFragmemt();
					    
					    // Insert the fragment by replacing any existing fragment
					    FragmentManager fragmentManager = getSupportFragmentManager();
					    fragmentManager.beginTransaction()
					                   .replace(R.id.content_frame, fragment)
					                   .commit();

					    // Highlight the selected item, update the title, and close the drawer
					    mDrawerList.setItemChecked(mNavigationDrawerListItems.size(), true);
					    mTitle = getString(R.string.label_search_car);
					    mDrawerLayout.closeDrawer(mDrawerList);
					}
				}
        		));

        
        // Create & Add Items "label_convenient"
        mNavigationDrawerListItems.add(new NavigationDrawerListItem(
        		NavigationDrawerListItem.SCREEN_IN_CATEGORY,
        		R.drawable.ic_nd_convenient,
        		this.getString(R.string.label_convenient),
        		"",
        		new OnNavigationDrawerItemClickHandler() {
					
					@Override
					public void doAction() {
						// open Maps
						Intent intent = new Intent(getApplicationContext(), GoogleMapsActivity.class);
						startActivity(intent);
					}
				}
        		));

        
        // Create & Add Items "label_post_newsfeed"
        mNavigationDrawerListItems.add(new NavigationDrawerListItem(
        		NavigationDrawerListItem.SCREEN_IN_CATEGORY,
        		R.drawable.ic_nd_newsfeed,
        		this.getString(R.string.label_post_newsfeed),
        		"",
        		new OnNavigationDrawerItemClickHandler() {
					
					@Override
					public void doAction() {
						// Create a new fragment and specify the PostNewFeed to show based on position
					    Fragment fragment = new PostNewFeedFragmemt();
					    
					    // Insert the fragment by replacing any existing fragment
					    FragmentManager fragmentManager = getSupportFragmentManager();
					    fragmentManager.beginTransaction()
					                   .replace(R.id.content_frame, fragment)
					                   .commit();

					    // Highlight the selected item, update the title, and close the drawer
					    mDrawerList.setItemChecked(mNavigationDrawerListItems.size(), true);
					    mTitle = getString(R.string.label_post_newsfeed);
					    mDrawerLayout.closeDrawer(mDrawerList);
					}
				}
        		));
        
        // Create & Add Items CATEGORY "4CARS"
        mNavigationDrawerListItems.add(new NavigationDrawerListItem(
        		NavigationDrawerListItem.CATEGORY_NAME,
        		0,
        		this.getString(R.string.label_4cars),
        		"",
        		null
        		));

        // Create & Add Items "label_account"
        mNavigationDrawerListItems.add(new NavigationDrawerListItem(
        		NavigationDrawerListItem.SCREEN_IN_CATEGORY,
        		R.drawable.ic_nd_account,
        		this.getString(R.string.label_account),
        		"",
        		new OnNavigationDrawerItemClickHandler() {
					
					@Override
					public void doAction() {
						Fragment fragment = null;
						
						if (ConfigureData.isLogged) {
							// Create a new fragment and specify the Account Info to show based on position
							fragment = new AccountFragmemt();
							mTitle = getString(R.string.label_account);
						} else {
							fragment = new LoginFragmemt();
							mTitle = getString(R.string.label_login);
						}
					    
					    // Insert the fragment by replacing any existing fragment
					    FragmentManager fragmentManager = getSupportFragmentManager();
					    fragmentManager.beginTransaction()
					                   .replace(R.id.content_frame, fragment)
					                   .commit();
						
					    
					    // Highlight the selected item, update the title, and close the drawer
					    mDrawerList.setItemChecked(mNavigationDrawerListItems.size(), true);
					    mDrawerLayout.closeDrawer(mDrawerList);
					}
				}
        		));
        
        // Create & Add Items "label_introduce"
        mNavigationDrawerListItems.add(new NavigationDrawerListItem(
        		NavigationDrawerListItem.SCREEN_IN_CATEGORY,
        		R.drawable.ic_nd_introduce,
        		this.getString(R.string.label_introduce),
        		"",
        		new OnNavigationDrawerItemClickHandler() {
					
					@Override
					public void doAction() {
						// Create a new fragment and specify the Introduce to show based on position
					    Fragment fragment = new IntroduceFragmemt();
					    
					    // Insert the fragment by replacing any existing fragment
					    FragmentManager fragmentManager = getSupportFragmentManager();
					    fragmentManager.beginTransaction()
					                   .replace(R.id.content_frame, fragment)
					                   .commit();
					    
					    // Highlight the selected item, update the title, and close the drawer
					    mDrawerList.setItemChecked(mNavigationDrawerListItems.size(), true);
					    mTitle = getString(R.string.label_introduce);
					    mDrawerLayout.closeDrawer(mDrawerList);
					}
				}
        		));

        
        // Create & Add Items "label_contact"
        mNavigationDrawerListItems.add(new NavigationDrawerListItem(
        		NavigationDrawerListItem.SCREEN_IN_CATEGORY,
        		R.drawable.ic_nd_contact,
        		this.getString(R.string.label_contact),
        		"",
        		new OnNavigationDrawerItemClickHandler() {
					
					@Override
					public void doAction() {
						// Create a new fragment and specify the Contact to show based on position
					    Fragment fragment = new ContactFragmemt();
					    
					    // Insert the fragment by replacing any existing fragment
					    FragmentManager fragmentManager = getSupportFragmentManager();
					    fragmentManager.beginTransaction()
					                   .replace(R.id.content_frame, fragment)
					                   .commit();
					    
					    // Highlight the selected item, update the title, and close the drawer
					    mDrawerList.setItemChecked(mNavigationDrawerListItems.size(), true);
					    mTitle = getString(R.string.label_contact);
					    mDrawerLayout.closeDrawer(mDrawerList);
					}
				}
        		));
        
        // Show Search Car as default screen
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, new SearchCarFragmemt())
	                   .commit();

	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(mNavigationDrawerListItems.size(), true);
	    getSupportActionBar().setTitle(R.string.label_search_car);
        
        // Create NavigationDrawer List adapter
        mNavigationDrawerAdapter = new NavigationDrawerAdapter(this, mNavigationDrawerListItems);
        
        // Create adapter for the Items
        mDrawerList.setAdapter(mNavigationDrawerAdapter);
        
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);
        
        /**
         * 2. Setting for the main activity can be listen and handle
         * when NavigationDarwer Toggle(Open & Close)
         */
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            	getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) { 
            	getSupportActionBar().setTitle(getString(R.string.app_name)); 
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Enable ActionBar application icon to behave as action to toggle navigation drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
	}

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 // Pass the event to ActionBarDrawerToggle, if it returns
	     // true, then it has handled the application icon touch event
	     if (mDrawerToggle.onOptionsItemSelected(item)) {
	    	 return true;
	     }
	     // Handle your other action bar items...
	     return super.onOptionsItemSelected(item);
	 }
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		try {
			mNavigationDrawerListItems.get(position).getOnItemClickHandler().doAction();
		} catch (NullPointerException e) {
			
		}
	}
	
	
	/**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


}