1.使用方式

     GPreviewBuilder.from(GridViewCustomActivity.this)//activity实例必须
                            .to(CustomActivity.class)//自定义Activity 使用默认的预览不需要
                            .setData(mThumbViewInfoList)//集合
                            .setUserFragment(UserFragment.class)//自定义Fragment 使用默认的预览不需要
                            .setCurrentIndex(position)
                            .setSingleFling(false)//是否在黑屏区域点击返回
                            .setDrag(false)//是否禁用图片拖拽返回  
                            .setType(GPreviewBuilder.IndicatorType.Dot)//指示器类型
                            .start();//启动            

2.列表控件item点击事件添加相应代码。 (RecyclerView为例，demo有(ListView和GridView和九宫格控件实例代码))

     mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
              //在你点击时，调用computeBoundsBackward（）方法
                computeBoundsBackward(mGridLayoutManager.findFirstVisibleItemPosition());
              GPreviewBuilder.from(RecycleViewActivity.this)
                                .setData(mThumbViewInfoList)
                                .setCurrentIndex(position)
                                .setType(GPreviewBuilder.IndicatorType.Number)
                                .start();
            }
        });
    /**
     ** 查找信息
     * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
     */
    private void computeBoundsBackward(int firstCompletelyVisiblePos) {
        for (int i = firstCompletelyVisiblePos;i < mThumbViewInfoList.size(); i++) {
            View itemView = mGridLayoutManager.findViewByPosition(i);
            Rect bounds = new Rect();
            if (itemView != null) {
                ImageView thumbView = (ImageView) itemView.findViewById(R.id.iv);
                thumbView.getGlobalVisibleRect(bounds);
            }
            mThumbViewInfoList.get(i).setBounds(bounds);
        }
    }
2.构造实体类： 你的实体类实现IThumbViewInfo接口

public class UserViewInfo implements IThumbViewInfo {
    private String url;  //图片地址
    private Rect mBounds; // 记录坐标
    private String user;//

    public UserViewInfo(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {//将你的图片地址字段返回
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    @Override
    public Rect getBounds() {//将你的图片显示坐标字段返回
        return mBounds;
    }
    
    public void setBounds(Rect bounds) {
        mBounds = bounds;
    }
   } 
3.使用自定义图片加载配置 注意这个必须实现哦。不然加载

1在你项目工程，创建一个类 实现接口IZoomMediaLoader接口 如下代码 demo 采用glide ，可以使用Picassor Imagloader 图片加载框架
public class TestImageLoader implements IZoomMediaLoader {
    @Override
    public void displayImage(Fragment context, String path, final MySimpleTarget<Bitmap> simpleTarget) {
         Glide.with(context).load(path).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                      .error(R.drawable.ic_default_image)
                      .into(new SimpleTarget<Bitmap>() {
                          @Override
                          public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                              simpleTarget.onResourceReady(resource);
                          }
                          @Override
                          public void onLoadStarted(Drawable placeholder) {
                              super.onLoadStarted(placeholder);
                              simpleTarget.onLoadStarted();
                          }
      
                          @Override
                          public void onLoadFailed(Exception e, Drawable errorDrawable) {
                              super.onLoadFailed(e, errorDrawable);
                              simpleTarget.onLoadFailed(errorDrawable);
                          }
                      });
    }
     @Override
     public void onStop(@NonNull Fragment context) {
           Glide.with(context).onStop();
     }
     @Override
     public void clearMemory(@NonNull Context c) {
              Glide.get(c).clearMemory();
     }

2注册 你实现自定义类，在你 app onCreate() 中
    @Override
      public void onCreate() {
          super.onCreate();
          ZoomMediaLoader.getInstance().init(new TestImageLoader());
      }
4.自定义Activity,Fragment

1.实现自定义Activity，实现你业务操作例如加入标题栏，ViewPager切换动画等等 .image.png

在你的布局中,引用类库核心布局

2.实现自定义Fragment 实现自定义业务 例如 长按保存图片，编辑图片,对图片说明内容等等 图片缩放效果采用PhotoView image.png

需要布局自定义重写onCreateView()。引用你自定义布局中添加
3 使用细节注意：

1 Activity和Fragment可以单独使用,也可以组合一起使用
自定义使用布局时，不在子类使用setContentView()方法
你在Activity 重写 setContentLayout()，返回你的自定义布局
在你布局内容 使用include layout="@layout/activity_image_preview_photo" 预览布局添加你布局中
GPreviewBuilder 调用 from()方法后，调用to();指向你.to(CustomActivity.class)自定义预览activity
别忘了在AndroidManifest activity 使用主题
示例：