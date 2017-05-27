package tech.jiangtao.support.kit.manager;

import android.content.Context;
import com.google.gson.Gson;
import io.realm.Realm;
import net.grandcentrix.tray.AppPreferences;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import tech.jiangtao.support.kit.SupportIM;
import tech.jiangtao.support.kit.callback.IMListenerCollection;
import tech.jiangtao.support.kit.eventbus.IMLoginRequestModel;
import tech.jiangtao.support.kit.eventbus.IMLoginResponseModel;
import tech.jiangtao.support.kit.eventbus.UnRegisterEvent;
import tech.jiangtao.support.kit.model.Account;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Class: IMAccountManager </br>
 * Description: 账户管理器 </br>
 * Creator: kevin </br>
 * Email: jiangtao103cp@gmail.com </br>
 * Date: 27/05/2017 02:42</br>
 * Update: 27/05/2017 02:42 </br>
 **/

public class IMAccountManager {

  public static final String TAG = IMAccountManager.class.getSimpleName();
  private AppPreferences mAppPreferences;
  private IMLoginRequestModel mLoginParam;
  private Realm mRealms;

  private IMListenerCollection.IMLoginListener mIMLoginListener;

  public IMAccountManager(Context context){
    if (!HermesEventBus.getDefault().isRegistered(this)) {
      HermesEventBus.getDefault().register(this);
    }
    if (mRealms==null){
      mRealms = Realm.getDefaultInstance();
    }
    mAppPreferences = new AppPreferences(context);
  }

  public void login(IMLoginRequestModel param,IMListenerCollection.IMLoginListener callBack){
    mIMLoginListener = callBack;
    mLoginParam  = param;
    HermesEventBus.getDefault().postSticky(param);
  }

  /**
   * 回调
   * @param event
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onLoginEvent(IMLoginResponseModel event){
    if (mIMLoginListener!=null) {
      if (event.result == null) {
        Account account = event.account;
        mAppPreferences.put(SupportIM.USER_ID, account.userId);
        mAppPreferences.put(SupportIM.USER_NAME, account.nickName);
        mAppPreferences.put(SupportIM.USER_REAL_NAME, mLoginParam.username);
        Gson gson = new Gson();
        String value = gson.toJson(account);
        mAppPreferences.put(SupportIM.USER, value);
        mIMLoginListener.loginSuccess(event.account);
      } else {
        mIMLoginListener.loginFailed(event.result);
      }
    }
  }

  /**
   * 注销账户
   * @param callBack
   */
  public void disConnect(IMListenerCollection.IMExitlistener callBack) {
    HermesEventBus.getDefault().post(new UnRegisterEvent());
    mRealms.executeTransactionAsync(realm -> realm.deleteAll(), () -> {
      callBack.exitSuccess();
      mAppPreferences.put(SupportIM.USER,null);
      mRealms.close();
    }, error -> {
      mRealms.close();
    });
  }

  /**
   * 获取账户信息
   * @return
   */
  public Account account(){
    String accountGson = mAppPreferences.getString(SupportIM.USER,null);
    Gson gson = new Gson();
    return gson.fromJson(accountGson,Account.class);
  }
}
