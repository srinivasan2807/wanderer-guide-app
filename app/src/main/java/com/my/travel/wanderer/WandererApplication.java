package com.my.travel.wanderer;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.bpackingapp.vietnam.travel.R;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.text.SimpleDateFormat;
import java.util.Locale;

import az.openweatherapi.OWService;
import az.openweatherapi.utils.OWSupportedUnits;
import com.my.travel.wanderer.data.AppSetting;
import com.my.travel.wanderer.data.AppState;
import com.my.travel.wanderer.database.RealmDB;
import com.my.travel.wanderer.service.CategoryInfoService;
import com.my.travel.wanderer.service.CategoryService;
import com.my.travel.wanderer.service.ChangeEventListener;
import com.my.travel.wanderer.service.CityService;
import com.my.travel.wanderer.service.CountryService;
import com.my.travel.wanderer.service.FacilityService;
import com.my.travel.wanderer.service.LanguageService;
import com.my.travel.wanderer.service.SubCategoryService;
import com.my.travel.wanderer.service.UserService;
import com.my.travel.wanderer.utils.FontUtils;
import com.my.travel.wanderer.utils.LoggerFactory;
import com.my.travel.wanderer.utils.MSharedPreferences;
import com.my.travel.wanderer.utils.Utils;
import com.bpackingapp.vietnam.travel.BuildConfig;

public class WandererApplication extends Application {
    MSharedPreferences mSharedPreferences;
    Context mContext;

    public static WandererApplication tipApplication;

    public static WandererApplication getTipApplication() {
        return tipApplication;
    }

    public static DisplayImageOptions defaultOptions;
    public static DisplayImageOptions defaultOptionsAvatar;
    public static DisplayImageOptions defaultOptionsNotCacheMem;

    public OWService mOWService;
    public Locale mLocale;
    public SimpleDateFormat dayFormat;
    public SimpleDateFormat weatherDateStampFormat;

    private DatabaseReference mDatabase;
    public LanguageService languageService;
    public CountryService countryService;
    public SubCategoryService subCategoryService;
    public CategoryInfoService categoryInfoService;
    public FacilityService facilityService;
    public UserService userService;
    public CategoryService categoryService;
    public CityService cityService;

    public static  RealmDB realmDB;
    public boolean changeSubCategoryComplete = false;



    public void onCreate() {
        super.onCreate();
        RealmDB.init(this);
//        RealmConfiguration config = new RealmConfiguration.Builder().build();
//        realmDB.setDefaultConfiguration(config);

//        MultiDex.install(this);
        mContext = WandererApplication.this;
        tipApplication = this;

        try {
            if (Build.VERSION.SDK_INT > 25) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseApp.initializeApp(this);
        FacebookSdk.sdkInitialize(this);

        LoggerFactory.init(getApplicationContext());




        FontUtils.loadFontThin(getApplicationContext(), "Roboto/Roboto-Thin.ttf");
        FontUtils.loadFont(getApplicationContext(), "Roboto/Roboto-Light.ttf");
        FontUtils.loadFontNormal(getApplicationContext(), "Roboto/Roboto-Regular.ttf");
        FontUtils.loadFontBold(getApplicationContext(), "Roboto/Roboto-Bold.ttf");

        mSharedPreferences = MSharedPreferences.getInstance(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            AppState.currentFireUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        AppSetting.getInstant(mContext).setOpenCount(AppSetting.getInstant(mContext).getOpenCount()+1);

        languageService = new LanguageService();
        languageService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {
                LoggerFactory.d("languageService onChildChanged");
            }

            @Override
            public void onDataChanged() {
                LoggerFactory.d("languageService onDataChanged");
                if(languageService.getCount() > 0) {
                    LoggerFactory.d("languageService = " +languageService.getItem(0).getKey());
                    countryService = new CountryService(WandererApplication.getTipApplication().languageService.getItem(0).getKey());
                    countryService.setOnChangedListener(new ChangeEventListener() {
                        @Override
                        public void onChildChanged(EventType type, int index, int oldIndex) {

                        }

                        @Override
                        public void onDataChanged() {
                            LoggerFactory.d("countryService getCount = " +countryService.getCount());
                            LoggerFactory.d("countryService = " +countryService.getItem(0).getKey());

                            cityService = new CityService(countryService.getItem(0).getKey());
                            cityService.setOnChangedListener(new ChangeEventListener() {
                                @Override
                                public void onChildChanged(EventType type, int index, int oldIndex) {

                                }

                                @Override
                                public void onDataChanged() {

                                }

                                @Override
                                public void onCancelled(DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                } else {
                    LoggerFactory.d("languageService emplty");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        subCategoryService = new SubCategoryService();
        subCategoryService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {
                changeSubCategoryComplete = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        categoryInfoService = new CategoryInfoService();
        categoryInfoService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {
                categoryInfoService.isLoaded = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        facilityService = new FacilityService();
        facilityService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        userService = new UserService();
        userService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {
                userService.isLoaded = true;
            }

            @Override
            public void onDataChanged() {
                userService.isLoaded = true;
                if(userService.getCount() > 0 && AppState.currentFireUser != null) {
                    LoggerFactory.d("############################# UPDATE CURRENT USER ###############################");
                    AppState.currentBpackUser = userService.getUserById(AppState.currentFireUser.getUid());
                    if (AppState.currentBpackUser != null) {
                        LoggerFactory.d(AppState.currentBpackUser.toString());
                        LoggerFactory.d("########################## LOAD User info firebase #########################");
                        LoggerFactory.d("AppState.currentBpackUser email" + AppState.currentBpackUser.getEmail());
                        LoggerFactory.d("AppState.currentBpackUser avatar" + AppState.currentBpackUser.getAvatar());
                        LoggerFactory.d("AppState.currentBpackUser name" + AppState.currentBpackUser.getName());
                        LoggerFactory.d("AppState.currentBpackUser user id" + AppState.currentBpackUser.getUserId());

                        LoggerFactory.d("########################## LOAD User info firebase #########################");
                    } else {
                        LoggerFactory.e("USER NOT FOUND: " + AppState.currentFireUser.getUid());
                    }
                    LoggerFactory.d("############################# UPDATE CURRENT USER ###############################");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        categoryService = new CategoryService();
        categoryService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });




//        initOneSignal();

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);

//        initCustomCrashHandler();

        Utils.getKeyHash(mContext);

        initImageLoader(this);

        mOWService = new OWService("f68bff9ead757dae52583e0ab5ce908c");
        mOWService.setLanguage(getResources().getConfiguration().locale);
        mOWService.setMetricUnits(OWSupportedUnits.METRIC);
        mLocale = getResources().getConfiguration().locale;
        dayFormat = new SimpleDateFormat("EEE", mLocale);
        weatherDateStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);

//        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
//        serviceIntent.setPackage("com.android.vending");
//        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public void changeSubCategory(String subCategoryKey) {
        changeSubCategoryComplete = false;
        subCategoryService = new SubCategoryService(subCategoryKey);
        subCategoryService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {
                changeSubCategoryComplete = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        clearMemoryCache();
    }

    public void clearMemoryCache(){
        try {
            LoggerFactory.i("clearMemoryCache...");
//            BitmapAjaxCallback.clearCache();
            ImageLoader.getInstance().clearMemoryCache();
        }catch (Exception e){
            LoggerFactory.logStackTrace(e);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        clearMemoryCache();
    }

    private void setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
    }

    public static void initImageLoader(Context context) {
        try {
            ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
            config.memoryCacheExtraOptions(480, 800); // default = device screen dimensions
            config.diskCacheExtraOptions(480, 800, null);
            config.threadPriority(Thread.NORM_PRIORITY - 2);
            config.denyCacheImageMultipleSizesInMemory();
            config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
            config.diskCacheSize(100 * 1024 * 1024); // 128 MiB
            config.diskCacheFileCount(500);
            config.memoryCache(new LruMemoryCache(2 * 1024 * 1024));
            config.memoryCacheSize(2 * 1024 * 1024);
            config.tasksProcessingOrder(QueueProcessingType.LIFO);
            config.threadPriority(1);

//        config.writeDebugLogs(); // Remove for release app
            ImageLoader.getInstance().init(config.build());

            defaultOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.img_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true).build();

            defaultOptionsAvatar = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.avatar_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true).build();

            defaultOptionsNotCacheMem = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).build();


        } catch (Exception e) {
            LoggerFactory.logStackTrace(e);
        }
    }

    public void logout(){
        try {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            LoggerFactory.logStackTrace(e);
        }
    }
}
