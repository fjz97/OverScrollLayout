# OverScrollLayout

仿豆瓣书影音详情页横向滑动加载更多

## 效果

![gif](gif.gif)

## 使用

布局文件中使用OverScrollLayout嵌套需要实现效果的RecyclerView
```
<com.fjz97.overscrolllayout.OverScrollLayout
            android:id="@+id/osl"
            android:layout_width="match_parent"
            android:layout_height="150dp">

        <android.support.v7.widget.RecyclerView
                android:id="@+id/rv"
                android:layout_width="match_parent"
                android:layout_height="150dp"
                android:overScrollMode="never" />

    </com.fjz97.overscrolllayout.OverScrollLayout>
```

回调
```
osl.setOnOverScrollReleaseListener(() -> {
        //callback
});
```