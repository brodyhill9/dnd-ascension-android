package com.example.dndascension.utils

import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView

fun setListViewHeightBasedOnChildren(listView: ListView) {
    val listAdapter = listView.getAdapter()
        ?: // pre-condition
        return

    var totalHeight = listView.getPaddingTop() + listView.getPaddingBottom()

    for (i in 0 until listAdapter.getCount()) {
        val listItem = listAdapter.getView(i, null, listView)
        if (listItem is ViewGroup) {
            listItem.setLayoutParams(
                AbsListView.LayoutParams(
                    AbsListView.LayoutParams.WRAP_CONTENT,
                    AbsListView.LayoutParams.WRAP_CONTENT
                )
            )
        }

        listItem.measure(0, 0)
        totalHeight += listItem.getMeasuredHeight()
    }

    val params = listView.getLayoutParams()
    params.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1)
    listView.setLayoutParams(params)
}