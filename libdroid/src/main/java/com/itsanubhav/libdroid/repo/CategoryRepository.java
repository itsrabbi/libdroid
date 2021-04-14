package com.itsanubhav.libdroid.repo;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;


import com.itsanubhav.libdroid.model.category.Category;
import com.itsanubhav.libdroid.model.response.CategoryResponse;
import com.itsanubhav.libdroid.network.ApiClient;
import com.itsanubhav.libdroid.network.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {

    private static final boolean HIDE_EMPTY_CATEGORIES = true;

    private static CategoryRepository categoryRepository;
    private ApiInterface apiInterface;

    public static CategoryRepository getInstance(){
        if (categoryRepository==null){
            categoryRepository = new CategoryRepository();
        }
        return categoryRepository;
    }

    public CategoryRepository() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public MutableLiveData<CategoryResponse> getCategories(int page, String search, Integer parent){
        final MutableLiveData<CategoryResponse> categories = new MutableLiveData<>();
        Call<List<Category>> call = apiInterface.getCategoryList(page,search,parent,HIDE_EMPTY_CATEGORIES,"wordroid");
        Log.e("Making Request", call.request().url().toString());
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()&&response.body()!=null){
                    try{
                        Log.d("Making Request",response.body().size()+" categories loaded");
                        int totalCategoryPages = Integer.parseInt(response.headers().get("x-wp-totalpages"));
                        List<Category> tempCat = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++) {
                            if(!response.body().get(i).isHidden()){
                                tempCat.add(response.body().get(i));
                            }
                        }
                        categories.postValue(new CategoryResponse(tempCat,totalCategoryPages));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
        return categories;
    }
}
