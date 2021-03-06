package tech.jiangtao.support.kit.api.service;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import tech.jiangtao.support.kit.realm.ContactRealm;
import tech.jiangtao.support.kit.model.User;
import tech.jiangtao.support.kit.model.group.Friends;
import tech.jiangtao.support.kit.model.group.Groups;
import tech.jiangtao.support.kit.model.group.InvitedInfo;

/**
 * Created by Vurtex on 2017/3/29.
 */

public interface UserServiceApi {

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("user/queryUserList")
    Observable<List<Friends>> queryUserList(@Field("userId") String userId);

    /**
     * 和上边一样
     */
    @FormUrlEncoded
    @POST("user/queryUserList")
    Observable<List<ContactRealm>> queryUserFriends(@Field("userId") String userId);

    /**
     * 获取我加入的所有群组
     * @param uid
     * @return
     */
    @GET("muc/joined")
    Observable<List<Groups>> getOwnGroup(@Query("uid") String uid);


    /**
     * 查找用户
     * @param nickname
     * @return
     */
    @GET("user/queryAccount")
    Observable<List<User>> getQueryAccount(@Query("nickname") String nickname);


    /**
     * 查找群组
     * @param roomName
     * @return
     */
    @GET("muc/search")
    Observable<List<Groups>> getQueryGroup(@Query("roomName") String roomName);


    /**
     * 查找群组
     * @param userId
     * @return
     */
    @POST("user/allInvite")
    Observable<List<InvitedInfo>> getAllInvite(@Query("userId") String userId);

    /**
     * 获取自己的信息
     * @param userId
     * @return
     */
    @POST("user/queryUser")
    @FormUrlEncoded
    Observable<User> selfAccount(@Field("userId") String userId);
}
