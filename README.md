# OverScrollLayout

仿豆瓣书影音详情页横向滑动加载更多

## 效果

<img src="gif.gif" height="585" width="270" />

## 使用

布局文件中使用OverScrollLayout嵌套需要实现效果的RecyclerView
```
<com.fjz97.overscrolllayout.OverScrollLayout
    android:id="@+id/osl"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    overscrolllayout:animDuration="400"
    overscrolllayout:damping="0.3"
    overscrolllayout:overScrollChangeText="释放查看"
    overscrolllayout:overScrollColor="#F5F5F5"
    overscrolllayout:overScrollSize="120"
    overscrolllayout:overScrollStateChangeSize="96"
    overscrolllayout:overScrollText="查看更多"
    overscrolllayout:textColor="#CDCDCD"
    overscrolllayout:textDamping="0.2"
    overscrolllayout:textSize="11sp">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:overScrollMode="never" />

</com.fjz97.overscrolllayout.OverScrollLayout>
```

回调
```
osl.setOnOverScrollReleaseListener(() -> {
        //callback
});
```