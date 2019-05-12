package com.example.admin.techkid_session2.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.admin.techkid_session2.R;
import com.example.admin.techkid_session2.database.DatabaseManager;
import com.example.admin.techkid_session2.database.model.CategoryModel;
import com.example.admin.techkid_session2.database.model.TopicModel;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class ToeicExpandableListViewAdapter extends BaseExpandableListAdapter {

    List<CategoryModel> categoryModelList;
    HashMap<String, List<TopicModel>> topicModelHashMap;
    Context context;

    public ToeicExpandableListViewAdapter(List<CategoryModel> categoryModelList, HashMap<String, List<TopicModel>> topicModelHashMap, Context context) {
        this.categoryModelList = categoryModelList;
        this.topicModelHashMap = topicModelHashMap;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return categoryModelList.size();
    }

    //index cua children trong group
    @Override
    public int getChildrenCount(int i) {
        return topicModelHashMap.get(categoryModelList.get(i).name).size();
    }

    @Override
    public Object getGroup(int i) {
        return categoryModelList.get(i);
    }

    //i: index group, i1: index children
    @Override
    public Object getChild(int i, int i1) {
        return topicModelHashMap.get(categoryModelList.get(i).name).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        view = layoutInflater.inflate(R.layout.item_list_category, viewGroup, false);

        CategoryModel categoryModel = (CategoryModel) getGroup(i);
        TextView tvCategory = view.findViewById(R.id.tv_category);
        TextView tvCategoryDes = view.findViewById(R.id.tv_category_des);
        ImageView ivArrow = view.findViewById(R.id.iv_arrow);
        CardView cvCategory = view.findViewById(R.id.cv_category);

        cvCategory.setCardBackgroundColor(Color.parseColor(categoryModel.color));

        if(isExpanded) {
            ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        } else {
            ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        }

        tvCategory.setText(categoryModel.name);

        String des = "";
        for(int j = 0; j < 5; j++){
            des += topicModelHashMap.get(categoryModel.name).get(j).name;
            if( j != 4) {
                des += " , ";
            }
        }
        tvCategoryDes.setText(des);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        view = layoutInflater.inflate(R.layout.item_list_topic, viewGroup, false);

        TopicModel topicModel = (TopicModel) getChild(i, i1);
        TextView tvTopic = view.findViewById(R.id.tv_name_topic);
        TextView tvLastTime = view.findViewById(R.id.tv_last_time);
        ProgressBar pbTopic = view.findViewById(R.id.pb_topic);

        tvTopic.setText(topicModel.name);
        if(topicModel.lastTime != null){
            tvLastTime.setText(topicModel.lastTime);
        }

        pbTopic.setMax(12);
        pbTopic.setProgress(DatabaseManager.getInstance(context).getNumOfMasterWordByTopicId(topicModel.id));
        pbTopic.setSecondaryProgress(12 - DatabaseManager.getInstance(context).getNumOfNewWordByTopicId(topicModel.id));

        return view;
    }

    //check xem co bam duoc vao topic ben trong khong ?
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void refreshList(Context context) {
        //1.change data
        topicModelHashMap.clear();
        topicModelHashMap.putAll(DatabaseManager.getInstance(context).
                getHashMapTopic(DatabaseManager.getInstance(context).getListTopic(),
                        categoryModelList));
        //2.refresh data : add, remove, addAll, removeAll, clear, ...
        notifyDataSetChanged();
    }
}
