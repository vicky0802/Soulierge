package com.zk.soulierge.utlities;




import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zk.soulierge.R;
import com.zk.soulierge.fragments.BaseFragment;
import com.zk.soulierge.fragments.FragmentAnimationType;


/**
 * Created by Zeera on 9/24/2017 bt ${File}
 */

public class FragmentUtility {
    private BaseFragment replaceToFragment;
    private FragmentAnimationType animationType = FragmentAnimationType.NO_ANIMATION;
    private int containerId = R.id.fl_fragment_container;//default fragment id
    private boolean isToAddToStack = false;
    private String setTag = "";
    private String backStackTag = "";
    private FragmentManager manager;

    private FragmentUtility(FragmentManager manager) {
        //no instance
        this.manager = manager;
    }



    public static FragmentUtility withManager(FragmentManager manager) {
        return new FragmentUtility(manager);
    }

    public FragmentUtility intoContainerId(int id) {
        this.containerId = id;
        return this;
    }


    public FragmentUtility withAnimationType(FragmentAnimationType type) {
        this.animationType = type;
        return this;
    }

    public void replaceToFragment(BaseFragment fragment) {
        this.replaceToFragment = fragment;
        replace();
    }

   /* public FragmentUtility setTag(String tag) {
        this.setTag = tag;
        return this;
    }*/

    public FragmentUtility addToBackStack(String tag) {
        isToAddToStack = true;
        backStackTag = tag;
        return this;
    }

    private void replace() {
        if (manager != null && containerId != 0 && replaceToFragment != null) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            setAnimationFragment(animationType, fragmentTransaction);
            setTag = replaceToFragment.getTagFragment();
            fragmentTransaction.replace(containerId, replaceToFragment, setTag);
            if (isToAddToStack)
                fragmentTransaction.addToBackStack(backStackTag);

            try {
                fragmentTransaction.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void setAnimationFragment(FragmentAnimationType type, FragmentTransaction ft) {
        switch (type) {
            case NO_ANIMATION:
                break;
            case SLIDE_FROM_LEFT:
                ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,
                        R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                break;
            case FADE_IN:
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case GROW_FROM_BOTTOM:
                ft.setCustomAnimations(R.anim.from_bottom, android.R.anim.fade_out,
                        android.R.anim.fade_in, R.anim.to_bottom);
                break;
            case SLIDE_FROM_RIGHT:
                ft.setCustomAnimations(R.anim.slide_in_from_right,R.anim.slide_out_to_left,
                        R.anim.slide_in_from_left,R.anim.slide_out_to_right);
        }
    }


    @Nullable
    public static BaseFragment getCurrentFragment(AppCompatActivity activity, int containerID) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentById(containerID);
        if (fragment instanceof BaseFragment)
            return ((BaseFragment) fragment);
        return null;
    }
}
