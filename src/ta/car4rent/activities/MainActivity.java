package ta.car4rent.activities;

import java.util.ArrayList;

import ta.car4rent.R;
import ta.car4rent.adapters.NavigationDrawerAdapter;
import ta.car4rent.adapters.NavigationDrawerListItem;
import ta.car4rent.adapters.OnNavigationDrawerItemClickHandler;
import ta.car4rent.adapters.TitleNavigationAdapter;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.fragments.AccountFragmemt;
import ta.car4rent.fragments.ContactFragmemt;
import ta.car4rent.fragments.IntroduceFragmemt;
import ta.car4rent.fragments.LoginFragmemt;
import ta.car4rent.fragments.ManageCarRequestesFragment;
import ta.car4rent.fragments.SearchCarFragmemt;
import ta.car4rent.fragments.SettingWarnningRadiusFragmemt;
import ta.car4rent.objects.SpinnerNavItem;
import ta.car4rent.utils.StaticFunction;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * @author anhle
 * 
 */
public class MainActivity extends ActionBarActivity implements
		OnItemClickListener, OnClickListener, OnNavigationListener {

	private ArrayList<NavigationDrawerListItem> mNavigationDrawerListItems;
	private NavigationDrawerAdapter mNavigationDrawerAdapter;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String mTitle;

	public static MainActivity Instance;
	
	// Navigation drawer list item Application category
	NavigationDrawerListItem ndlAppCategory;
	NavigationDrawerListItem ndlSearchCar;
	NavigationDrawerListItem ndlConvenient;
	NavigationDrawerListItem ndlCarRequested;
	NavigationDrawerListItem ndlSetting;

	// Navigation drawer list item 4Cars category
	NavigationDrawerListItem ndl4CarsCategory;
	NavigationDrawerListItem ndlAccounnt;
	NavigationDrawerListItem ndlIntroduce;
	NavigationDrawerListItem ndlContact;
	
	// Title navigation Spinner data
    private ArrayList<SpinnerNavItem> navSpinner;
     
    // Navigation adapter
    private TitleNavigationAdapter titleNavigationAdapter;
	
	// For listen to Open & Close NavigationDrawer menu
	private ActionBarDrawerToggle mDrawerToggle;

	public void setActionBarTitle(String title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Instance = this;
		
		setContentView(R.layout.activity_main);
		// set activity for all fragment reference
		ConfigureData.activityMain = this;
		ConfigureData.loadSharedPreference();


		// Navigation menu for Filter
		// Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("Tin đang mở", R.drawable.ic_open_default));
        navSpinner.add(new SpinnerNavItem("Tin đã đóng", R.drawable.ic_close_default));
        navSpinner.add(new SpinnerNavItem("Tất cả tin đăng", R.drawable.ic_car_request_default));
         
        // title drop down adapter
        titleNavigationAdapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);
 
        // assigning the spinner navigation     
        getSupportActionBar().setListNavigationCallbacks(titleNavigationAdapter, this);
		
		/**
		 * 1. Initial for NavigationDrawer
		 */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mNavigationDrawerListItems = new ArrayList<NavigationDrawerListItem>();

		// Create & Add Items CATEGORY "label_application_session"
		ndlAppCategory = new NavigationDrawerListItem(
				NavigationDrawerListItem.CATEGORY_NAME, 0,
				this.getString(R.string.label_application_session), "", null);
		mNavigationDrawerListItems.add(ndlAppCategory);

		// Create & Add Items "label_products"
		ndlSearchCar = new NavigationDrawerListItem(
				NavigationDrawerListItem.SCREEN_IN_CATEGORY,
				R.drawable.ic_nd_search_car,
				this.getString(R.string.label_search_car), "",
				new OnNavigationDrawerItemClickHandler() {

					@Override
					public void doAction() {
						// Create a new fragment and specify the planet to show
						// based on position
						SearchCarFragmemt fragment = new SearchCarFragmemt();
						// Insert the fragment by replacing any existing
						// fragment
						FragmentManager fragmentManager = getSupportFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, fragment).commit();

						// Highlight the selected item, update the title, and
						// close the drawer
						mDrawerList.setItemChecked(
								mNavigationDrawerListItems.size(), true);
						mTitle = getString(R.string.label_search_car);
						mDrawerLayout.closeDrawer(mDrawerList);
						
						showActionFilterSpinner(false);
					}
				});
		mNavigationDrawerListItems.add(ndlSearchCar);

		// Create & Add Items "label_convenient"
		ndlConvenient = new NavigationDrawerListItem(
				NavigationDrawerListItem.SCREEN_IN_CATEGORY,
				R.drawable.ic_nd_convenient,
				this.getString(R.string.label_convenient), "",
				new OnNavigationDrawerItemClickHandler() {

					@Override
					public void doAction() {
						// open Maps
						Intent intent = new Intent(getApplicationContext(),
								GoogleMapsActivity.class);
						startActivity(intent);
						mDrawerList.setItemChecked(
								mNavigationDrawerListItems.size(), true);
						mDrawerLayout.closeDrawer(mDrawerList);
						
						showActionFilterSpinner(false);
					}
				});
		mNavigationDrawerListItems.add(ndlConvenient);

		// Create & Add Items "label_newsfeed"
		ndlCarRequested = new NavigationDrawerListItem(
				NavigationDrawerListItem.SCREEN_IN_CATEGORY,
				R.drawable.ic_nd_newsfeed,
				this.getString(R.string.label_newsfeed), "",
				new OnNavigationDrawerItemClickHandler() {

					@Override
					public void doAction() {
						Fragment fragment = null;
						if (ConfigureData.isLogged) {
							// Create a new fragment and specify the Account
							// Info to show based on position						
							fragment = new ManageCarRequestesFragment();
							showActionFilterSpinner(true);
						} else {
							Toast.makeText(getApplicationContext(),
									getString(R.string.need_login_to_view), 0)
									.show();
							ConfigureData.isCalledFromCarRequested = true;
							fragment = new LoginFragmemt();
						}

						mTitle = getString(R.string.label_newsfeed);
						
						// Insert the fragment by replacing any existing
						// fragment
						FragmentManager fragmentManager = getSupportFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, fragment).commit();

						// Highlight the selected item, update the title, and
						// close the drawer
						mDrawerList.setItemChecked(
								mNavigationDrawerListItems.size(), true);
						mDrawerLayout.closeDrawer(mDrawerList);
						

					}
				});
		mNavigationDrawerListItems.add(ndlCarRequested);

		// Create & Add Items "label_setting"
		ndlSetting = new NavigationDrawerListItem(
				NavigationDrawerListItem.SCREEN_IN_CATEGORY,
				R.drawable.ic_nd_settings,
				this.getString(R.string.label_setting), "",
				new OnNavigationDrawerItemClickHandler() {

					@Override
					public void doAction() {
						// Create a new fragment and specify the PostNewFeed to
						// show based on position
						ConfigureData.isPostNewsScreen = true;
						SettingWarnningRadiusFragmemt fragment = new SettingWarnningRadiusFragmemt();

						// Insert the fragment by replacing any existing
						// fragment
						FragmentManager fragmentManager = getSupportFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, fragment).commit();

						// Highlight the selected item, update the title, and
						// close the drawer
						mDrawerList.setItemChecked(
								mNavigationDrawerListItems.size(), true);
						mTitle = getString(R.string.label_setting);
						mDrawerLayout.closeDrawer(mDrawerList);
						
						showActionFilterSpinner(false);
					}
				});
		mNavigationDrawerListItems.add(ndlSetting);

		// Create & Add Items CATEGORY "4CARS"
		ndl4CarsCategory = new NavigationDrawerListItem(
				NavigationDrawerListItem.CATEGORY_NAME, 0,
				this.getString(R.string.label_4cars), "", null);
		mNavigationDrawerListItems.add(ndl4CarsCategory);

		// Create & Add Items "label_account"
		ndlAccounnt = new NavigationDrawerListItem(
				NavigationDrawerListItem.SCREEN_IN_CATEGORY,
				R.drawable.ic_nd_account,
				this.getString(R.string.label_account), "",
				new OnNavigationDrawerItemClickHandler() {

					@Override
					public void doAction() {
						Fragment fragment = null;

						if (ConfigureData.isLogged) {
							// Create a new fragment and specify the Account
							// Info to show based on position
							fragment = new AccountFragmemt();
						} else {

							fragment = new LoginFragmemt();
						}

						mTitle = getString(R.string.label_account);

						// Insert the fragment by replacing any existing
						// fragment
						FragmentManager fragmentManager = getSupportFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, fragment).commit();

						// Highlight the selected item, update the title, and
						// close the drawer
						mDrawerList.setItemChecked(
								mNavigationDrawerListItems.size(), true);
						mDrawerLayout.closeDrawer(mDrawerList);
						
						showActionFilterSpinner(false);
					}
				});
		mNavigationDrawerListItems.add(ndlAccounnt);

		// Create & Add Items "label_introduce"
		ndlIntroduce = new NavigationDrawerListItem(
				NavigationDrawerListItem.SCREEN_IN_CATEGORY,
				R.drawable.ic_nd_introduce,
				this.getString(R.string.label_introduce), "",
				new OnNavigationDrawerItemClickHandler() {

					@Override
					public void doAction() {
						// Create a new fragment and specify the Introduce to
						// show based on position
						Fragment fragment = new IntroduceFragmemt();

						// Insert the fragment by replacing any existing
						// fragment
						FragmentManager fragmentManager = getSupportFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, fragment).commit();

						// Highlight the selected item, update the title, and
						// close the drawer
						mDrawerList.setItemChecked(
								mNavigationDrawerListItems.size(), true);
						mTitle = getString(R.string.label_introduce);
						mDrawerLayout.closeDrawer(mDrawerList);
						
						showActionFilterSpinner(false);
					}
				});
		mNavigationDrawerListItems.add(ndlIntroduce);

		// Create & Add Items "label_contact"
		ndlContact = new NavigationDrawerListItem(
				NavigationDrawerListItem.SCREEN_IN_CATEGORY,
				R.drawable.ic_nd_contact,
				this.getString(R.string.label_contact), "",
				new OnNavigationDrawerItemClickHandler() {

					@Override
					public void doAction() {
						// Create a new fragment and specify the Contact to show
						// based on position
						Fragment fragment = new ContactFragmemt();

						// Insert the fragment by replacing any existing
						// fragment
						FragmentManager fragmentManager = getSupportFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, fragment).commit();

						// Highlight the selected item, update the title, and
						// close the drawer
						mDrawerList.setItemChecked(
								mNavigationDrawerListItems.size(), true);
						mTitle = getString(R.string.label_contact);
						mDrawerLayout.closeDrawer(mDrawerList);
						
						showActionFilterSpinner(false);
					}
				});
		mNavigationDrawerListItems.add(ndlContact);

		// Show Search Car as default screen
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, new SearchCarFragmemt()).commit();

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(mNavigationDrawerListItems.size(), true);
		getSupportActionBar().setTitle(R.string.label_search_car);

		// Create NavigationDrawer List adapter
		mNavigationDrawerAdapter = new NavigationDrawerAdapter(this,
				mNavigationDrawerListItems);

		// Create adapter for the Items
		mDrawerList.setAdapter(mNavigationDrawerAdapter);

		// Set the list's click listener
		mDrawerList.setOnItemClickListener(this);

		/**
		 * 2. Setting for the main activity can be listen and handle when
		 * NavigationDarwer Toggle(Open & Close)
		 */
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(getString(R.string.app_name));
				StaticFunction.hideKeyboard(MainActivity.this);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Enable ActionBar application icon to behave as action to toggle
		// navigation drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	public AlertDialog createAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Thoát ứng dụng ?");
		builder.setIcon(R.drawable.ic_launcher);

		builder.setPositiveButton("Thoát", this);
		builder.setNeutralButton("Hủy", this);
		return builder.create();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_POSITIVE) {
			finish();

		} else if (which == AlertDialog.BUTTON_NEUTRAL) {

		}
	}

	@Override
	public void onBackPressed() {
		if (ConfigureData.currentScreen == 2) {
			Fragment searchFragment = new SearchCarFragmemt();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, searchFragment).commit();
		} else if (ConfigureData.currentScreen == 1) {
			// AlertDialog dialog = createAlertDialog();
			// dialog.show();
		} else {
			super.onBackPressed();
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		try {
			mNavigationDrawerListItems.get(position).getOnItemClickHandler()
					.doAction();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.account_screen_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the application icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle presses on the action bar items
		switch (item.getItemId()) {
		/*
		 * case R.id.action_logout: ConfigureData.userAccount = null;
		 * ConfigureData.isLogged = false; ConfigureData.token = null;
		 * ConfigureData.userName = null; ConfigureData.password = null;
		 * 
		 * // Show Login Fragment LoginFragmemt fragment = new LoginFragmemt();
		 * mTitle = getString(R.string.label_login);
		 * 
		 * FragmentManager fragmentManager = getSupportFragmentManager();
		 * fragmentManager.beginTransaction().replace(R.id.content_frame,
		 * fragment).commit();
		 * 
		 * return true;
		 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showActionFilterSpinner(boolean isShow) {
		if (isShow) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		} else {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
		}
	}
	
	@Override
	public boolean onNavigationItemSelected(int pos, long arg1) {
		ManageCarRequestesFragment fragment = new ManageCarRequestesFragment();
		// Insert the fragment by replacing any existing
		// fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		switch (pos) {
		case 0:
			fragment.setFilterMode(ManageCarRequestesFragment.FILTER_MODE_OPEN);
			break;
		case 1:
			fragment.setFilterMode(ManageCarRequestesFragment.FILTER_MODE_CLOSED);
			break;
		case 2:
			fragment.setFilterMode(ManageCarRequestesFragment.FILTER_MODE_ALL);
			break;
		}
		
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		return false;
	}
}
