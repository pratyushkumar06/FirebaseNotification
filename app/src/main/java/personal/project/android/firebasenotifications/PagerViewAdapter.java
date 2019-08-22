package personal.project.android.firebasenotifications;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class PagerViewAdapter extends FragmentPagerAdapter {
    private String[] tabTitles = new String[]{"Profile", "Users", "Notifications"};
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ProfileFragment profileFragment=new ProfileFragment();
                return profileFragment;
            case 1:
                UsersFragment usersFragment=new UsersFragment();
                return usersFragment;

            case 2:
                NotificationsFragment notificationsFragment=new NotificationsFragment();
                return notificationsFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
