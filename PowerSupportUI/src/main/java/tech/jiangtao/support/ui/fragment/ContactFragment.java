package tech.jiangtao.support.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import com.kevin.library.widget.CleanDialog;
import com.kevin.library.widget.SideBar;
import com.kevin.library.widget.builder.IconFlag;
import com.kevin.library.widget.builder.PositiveClickListener;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.jiangtao.support.kit.eventbus.ContactEvent;
import tech.jiangtao.support.kit.eventbus.RosterEntryBus;
import tech.jiangtao.support.kit.realm.VCardRealm;
import tech.jiangtao.support.kit.util.LogUtils;
import tech.jiangtao.support.ui.R;
import tech.jiangtao.support.ui.R2;
import tech.jiangtao.support.ui.activity.ChatActivity;
import tech.jiangtao.support.ui.activity.GroupListActivity;
import tech.jiangtao.support.ui.activity.NewFriendActivity;
import tech.jiangtao.support.ui.adapter.ContactAdapter;
import tech.jiangtao.support.ui.adapter.EasyViewHolder;
import tech.jiangtao.support.ui.model.type.ContactType;
import tech.jiangtao.support.ui.pattern.ConstrutContact;
import tech.jiangtao.support.ui.utils.RecyclerViewUtils;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Class: ContactFragment </br>
 * Description: 通讯录页面 </br>
 * Creator: kevin </br>
 * Email: jiangtao103cp@gmail.com </br>
 * Date: 02/12/2016 11:42 AM</br>
 * Update: 02/12/2016 11:42 AM </br>
 **/
public class ContactFragment extends BaseFragment
    implements EasyViewHolder.OnItemClickListener, EasyViewHolder.OnItemLongClickListener,
    EasyViewHolder.OnItemLeftScrollListener,SwipeRefreshLayout.OnRefreshListener {

  @BindView(R2.id.contact_list) RecyclerView mContactList;
  @BindView(R2.id.sidebar) SideBar mSideBar;
  @BindView(R2.id.ui_view_bubble) TextView mUiViewBuddle;
  @BindView(R2.id.contact_swift_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
  public static final String TAG = ContactFragment.class.getSimpleName();
  private ContactAdapter mBaseEasyAdapter;
  private List<ConstrutContact> mConstrutContact;
  private Realm mRealm;
  private RealmResults<VCardRealm> mVCardRealmRealmResults;

  public static ContactFragment newInstance() {
    return new ContactFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    setRefresh();
    setAdapter();
    getContact();
    return getView();
  }

  private void setRefresh() {
    mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
        android.R.color.holo_green_light, android.R.color.holo_orange_light,
        android.R.color.holo_red_light);
    mSwipeRefreshLayout.setDistanceToTriggerSync(300);
    mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
    mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
    mSwipeRefreshLayout.setOnRefreshListener(this);
  }

  @Override public int layout() {
    return R.layout.fragment_contact;
  }

  public void setAdapter() {
    mConstrutContact = new ArrayList<>();
    mBaseEasyAdapter = new ContactAdapter(getContext(), mConstrutContact);
    mBaseEasyAdapter.setOnClickListener(this);
    mBaseEasyAdapter.setOnLongClickListener(this);
    mContactList.addItemDecoration(RecyclerViewUtils.buildItemDecoration(getContext()));
    mContactList.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    mContactList.setAdapter(mBaseEasyAdapter);
    HermesEventBus.getDefault().post(new ContactEvent());
  }

  @Override public void onResume() {
    super.onResume();
  }

  private void getContact() {
    if (mRealm == null || mRealm.isClosed()) {
      mRealm = Realm.getDefaultInstance();
    }
    mRealm.executeTransaction(realm -> {
      RealmQuery<VCardRealm> realmQuery = realm.where(VCardRealm.class);
      mVCardRealmRealmResults = realmQuery.equalTo("friend", true).findAllSorted("firstLetter");
      buildHeadView();
      LogUtils.d(TAG, "getContact: 打印出好友的数量:" + mVCardRealmRealmResults.size());
      for (int i = 0; i < mVCardRealmRealmResults.size(); i++) {
        if (mVCardRealmRealmResults != null
            && mVCardRealmRealmResults.get(i) != null
            && mVCardRealmRealmResults.get(i).getFirstLetter() != null) {
          if (i == 0) {
            mConstrutContact.add(new ConstrutContact.Builder().type(ContactType.TYPE_LETTER)
                .title(mVCardRealmRealmResults.get(i).getFirstLetter())
                .build());
          }
          if (i > 0) {
            if (mVCardRealmRealmResults.get(i - 1).getFirstLetter() != null
                && !(mVCardRealmRealmResults.get(i - 1)
                .getFirstLetter()
                .equals(mVCardRealmRealmResults.get(i).getFirstLetter()))) {
              mConstrutContact.add(new ConstrutContact.Builder().type(ContactType.TYPE_LETTER)
                  .title(mVCardRealmRealmResults.get(i).getFirstLetter())
                  .build());
            }
          }
        }
        mConstrutContact.add(new ConstrutContact.Builder().type(ContactType.TYPE_NORMAL)
            .vCardRealm(mVCardRealmRealmResults.get(i))
            .build());
      }
      mBaseEasyAdapter.notifyDataSetChanged();
      mVCardRealmRealmResults.addChangeListener(element -> {
        mConstrutContact.clear();
        buildHeadView();
        for (int i = 0; i < mVCardRealmRealmResults.size(); i++) {
          if (mVCardRealmRealmResults.get(i).getFirstLetter() != null) {
            if (i == 0) {
              mConstrutContact.add(new ConstrutContact.Builder().type(ContactType.TYPE_LETTER)
                  .title(mVCardRealmRealmResults.get(i).getFirstLetter())
                  .build());
            }
            if (i > 0) {
              if (mVCardRealmRealmResults.get(i - 1).getFirstLetter() != null
                  && !mVCardRealmRealmResults.get(i - 1)
                  .getFirstLetter()
                  .equals(mVCardRealmRealmResults.get(i).getFirstLetter())) {
                mConstrutContact.add(new ConstrutContact.Builder().type(ContactType.TYPE_LETTER)
                    .title(mVCardRealmRealmResults.get(i).getFirstLetter())
                    .build());
              }
            }
          }
          mConstrutContact.add(new ConstrutContact.Builder().type(ContactType.TYPE_NORMAL)
              .vCardRealm(mVCardRealmRealmResults.get(i))
              .build());
        }
        mBaseEasyAdapter.notifyDataSetChanged();
      });
    });
    buildSideBar();
  }

  public void buildSideBar() {
    mSideBar.setBubble(mUiViewBuddle);
    List<String> list = Arrays.asList(SideBar.b);
    mSideBar.setUpCharList(list);
    mSideBar.setOnTouchingLetterChangedListener(s -> {
      for (int i = 0; i < mConstrutContact.size(); i++) {
        if (mConstrutContact.get(i).mTitle != null) {
          if (s.equals(mConstrutContact.get(i).mTitle)) {
            mContactList.scrollToPosition(i + 2);
          }
        }
      }
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mRealm.close();
  }

  public void buildHeadView() {
    mConstrutContact.add(new ConstrutContact.Builder().type(ContactType.TYPE_GROUP)
        .id(R.mipmap.iconfont_qun)
        .title("群聊")
        .build());
    mConstrutContact.add(new ConstrutContact.Builder().type(ContactType.TYPE_GROUP)
        .id(R.mipmap.iconfont_pengyou)
        .title("新朋友")
        .build());
  }

  @Override public void onItemClick(int position, View view) {
    LogUtils.d(TAG, "onItemClick: ");
    if (position == 0) {
      GroupListActivity.startGroupList(getContext());
    } else if (position == 1) {
      NewFriendActivity.startNewFriend(getContext());
    } else {
      ChatActivity.startChat((Activity) getContext(), mConstrutContact.get(position).mVCardRealm);
    }
  }

  @Override public void onItemLeftClick(int position, View view) {
    LogUtils.d(TAG, "onItemLeftClick: ");
  }

  @Override public boolean onItemLongClick(int position, View view) {
    LogUtils.d(TAG, "onItemLongClick: ");
    ConstrutContact construtContact = mConstrutContact.get(position);
    if (position >= 2) {
      deleteFriends(construtContact.mVCardRealm.getJid(),
          construtContact.mVCardRealm.getNickName());
    }
    return false;
  }

  public void deleteFriends(String userjid, String username) {
    final CleanDialog dialog = new CleanDialog.Builder(getContext()).iconFlag(IconFlag.WARN)
        .negativeButton("取消", Dialog::dismiss)
        .positiveButton("删除", new PositiveClickListener() {
          @Override public void onPositiveClickListener(CleanDialog dialog1) {
            //删除用户,远程删除用户，成功后，从会话中列表中，删除用户
            HermesEventBus.getDefault().post(new RosterEntryBus(userjid));
            dialog1.dismiss();
          }
        })
        .title("确认删除好友" + username + "吗?")
        .negativeTextColor(Color.WHITE)
        .positiveTextColor(Color.WHITE)
        .builder();
    dialog.showDialog();
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onRefresh() {
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        mSwipeRefreshLayout.setRefreshing(false);
      }
    },3000);
  }
}
