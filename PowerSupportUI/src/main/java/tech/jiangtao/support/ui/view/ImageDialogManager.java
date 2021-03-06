package tech.jiangtao.support.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import tech.jiangtao.support.ui.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Class: ImageDialogManager </br>
 * Description: 图片查看器 </br>
 * Creator: kevin </br>
 * Email: jiangtao103cp@gmail.com </br>
 * Date: 31/12/2016 4:16 PM</br>
 * Update: 31/12/2016 4:16 PM </br>
 **/

public class ImageDialogManager {
  private Dialog mDialog;
  private Context mContext;
  private String mUrl;

  public ImageDialogManager(Context context, String url) {
    mContext = context;
    mUrl = url;
  }

  public void showDialog() {
    mDialog = new Dialog(mContext, R.style.AppTheme_Transparent);
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View view = inflater.inflate(R.layout.dialog_image, null);
    mDialog.setContentView(view);
    ImageView mPhotoImageView = (ImageView) view.findViewById(R.id.image_photo);
    //加载
    Glide.with(mContext)
        .load(Uri.parse(mUrl))
        .override(100, 100)
        .fitCenter()
        .error(R.mipmap.ic_mipmap_default_image)
        .placeholder(R.mipmap.ic_mipmap_default_image)
        .into(mPhotoImageView);
    PhotoViewAttacher mAttached = new PhotoViewAttacher(mPhotoImageView);
    mAttached.setScaleType(ImageView.ScaleType.FIT_CENTER);
    mDialog.show();
    mAttached.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
      @Override public void onPhotoTap(View view, float x, float y) {
        dismiss();
      }

      @Override public void onOutsidePhotoTap() {

      }
    });
  }

  public void dismiss() {
    if (mDialog != null) {
      mDialog.dismiss();
      mDialog = null;
    }
  }
}
