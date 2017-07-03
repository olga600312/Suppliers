package com.streetmarket.olga_pc.suppliers;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.streetmarket.olga_pc.suppliers.beans.Item;
import com.streetmarket.olga_pc.suppliers.dao.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class ItemListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ItemListAdapter adapter;

    public ItemListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ItemListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemListFragment newInstance() {
        return new ItemListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvResult);
        setupRecyclerView(rv);
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        //recyclerView.addItemDecoration(new SpacesItemDecoration(10));

        adapter = new ItemListAdapter();
        recyclerView.setAdapter(adapter);
        DatabaseHandler.Settings settings = new DatabaseHandler.Settings(getActivity());
        String str = settings.getValue("filtered");
        updateList(str == null || Integer.parseInt(str) < 0);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onItemPressed(String code) {
        if (mListener != null) {
            mListener.onItemListFragmentInteraction(code);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateList(boolean full) {
        Activity a = getActivity();
        if (a != null) {
            ArrayList<Item> list;
            if (full) {
                DatabaseHandler.Items results = new DatabaseHandler.Items(getActivity());
                list = results.retrieve(false);
            } else {
                DatabaseHandler.Results results = new DatabaseHandler.Results(getActivity());
                list = results.retrieve(false);
            }
            updateList(list);
        }
    }

    public void updateList(ArrayList<Item> list) {
        Activity a = getActivity();
        if (a != null && list != null) {
            adapter.updateData(list);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemListFragmentInteraction(String code);
    }

    private class ItemListAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private ArrayList<Item> data = new ArrayList<>();
        private DatabaseHandler.Suppliers suppliers = new DatabaseHandler.Suppliers(getActivity());

        public void updateData(ArrayList<Item> data) {
            this.data = data;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View vDefault = inflater.inflate(R.layout.list_item, parent, false);
            return new ItemViewHolder(vDefault);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            Item item = getValueAt(position);
            holder.mBoundString = item.getCode();
            holder.tvName.setText(item.getName());
            holder.tvBarcode.setText(item.getCode());
            holder.tvCount.setText(String.format("%d <-> %.2f", (int)item.getCount(),item.getCost()));
            String str = suppliers.getValue(item.getSupplier());
            holder.tvSupplier.setText(str != null ? str : "???");

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemPressed(holder.mBoundString);
                }
            });
            File photo = Utilities.getImageFile(holder.mImageView.getContext(), item.getCode());
            if (photo != null) {
            /*Bitmap b=BitmapFactory.decodeFile(photo.getAbsolutePath());
            imageView.setImageBitmap(b);
            // Now change ImageView's dimensions to match the scaled image
            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) imageView.getLayoutParams();
            params.width = b.getWidth();
            params.height = b.getHeight();
            imageView.setLayoutParams(params);*/

                Glide.with(holder.mImageView.getContext()).load(photo).fitCenter().into(holder.mImageView);
            } else
                Glide.with(holder.mImageView.getContext())
                        .load(R.drawable.business_application_72)
                        .fitCenter()
                        .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public Item getValueAt(int position) {
            return data.get(position);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public final View mView;
        public final ImageView mImageView;
        public final TextView tvName;
        public final TextView tvBarcode;
        public final TextView tvCount;
        public final TextView tvSupplier;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.avatar);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvBarcode = (TextView) view.findViewById(R.id.tvBarcode);
            tvCount = (TextView) view.findViewById(R.id.tvCount);
            tvSupplier = (TextView) view.findViewById(R.id.tvSupplier);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvName.getText();
        }
    }


    private static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Context context;

        private Drawable mDivider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            this.context=context;
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            mDivider.setTint(ContextCompat.getColor(context, R.color.primary_light));
            mDivider.setTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{ContextCompat.getColor(context, R.color.torchOff)}));
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            mDivider = ContextCompat.getDrawable(context, resId);
            mDivider.setTint(ContextCompat.getColor(context, R.color.primary_light));
            mDivider.setTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{ContextCompat.getColor(context, R.color.torchOff)}));

        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();


            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
               /* if (i % 2 == 0) {
                    int myColor =ContextCompat.getColor(context, R.color.primary_light);
                    Paint paint = new Paint();
                    paint.setColor(myColor);
                    c.drawRect(new RectF(child.getLeft(), child.getTop(), child.getRight(), child.getBottom()), paint);
                }*/
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;

        public SpacesItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = mSpace;
        }
    }
}

