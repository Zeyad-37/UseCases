package com.zeyad.usecases.app.components.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericRecyclerViewAdapter extends RecyclerView.Adapter<GenericRecyclerViewAdapter.ViewHolder> {

    public final LayoutInflater mLayoutInflater;
    private List<ItemInfo> mDataList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private SparseBooleanArray mSelectedItems;
    private boolean mIsLoadingFooterAdded = false, mHasHeader = false, mHasFooter = false, allowSelection = false,
            areItemsClickable = true, areItemsExpandable = false;
    private int expandedPosition = -1;

    public GenericRecyclerViewAdapter(Context context, List<ItemInfo> list) {
        validateList(list);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataList = list;
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemInfo itemInfo = mDataList.get(position);
        holder.bindData(itemInfo.getData(), mSelectedItems.get(position, false), position, itemInfo.isEnabled());
        if (areItemsClickable && !(hasHeader() && position == 0 || hasFooter() && position == mDataList.size() - 1)) {
            if (mOnItemClickListener != null)
                holder.itemView.setOnClickListener(view -> mOnItemClickListener.onItemClicked(holder
                        .getAdapterPosition(), itemInfo, holder));
            if (mOnItemLongClickListener != null)
                holder.itemView.setOnLongClickListener(view -> mOnItemLongClickListener
                        .onItemLongClicked(holder.getAdapterPosition(), itemInfo, holder));
        }
        if (areItemsExpandable) {
            final boolean isExpanded = position == expandedPosition;
            holder.expand(isExpanded);
            holder.itemView.setActivated(true);
            holder.itemView.setOnClickListener(view -> {
                expandedPosition = isExpanded ? -1 : position;
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader())
            return ItemInfo.HEADER;
        else if (position == mDataList.size() - 1 && mIsLoadingFooterAdded)
            return ItemInfo.LOADING;
        else if (position == mDataList.size() - 1 && !mIsLoadingFooterAdded && hasFooter())
            return ItemInfo.FOOTER;
        else
            return mDataList != null ? mDataList.get(position).getLayoutId() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getId();
    }

    public ItemInfo getItem(int index) {
        return mDataList.get(index);
    }

    public List<Long> getSelectedItemsIds() {
        ArrayList<Long> integers = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++)
            try {
                if (getSelectedItems().contains(i))
                    integers.add(mDataList.get(i).getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        return integers;
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    public int getPureSize() {
        return getPureDataList().size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public boolean hasItemById(long itemId) {
        for (ItemInfo itemInfo : mDataList)
            if (itemInfo.getId() == itemId)
                return true;
        return false;
    }

    public int getItemIndexById(long itemId) {
        for (int i = 0; i < mDataList.size(); i++)
            if (mDataList.get(i).getId() == itemId)
                return i;
        return -1;
    }

    public ItemInfo getItemById(long itemId) throws Exception {
        for (ItemInfo itemInfo : mDataList)
            if (itemInfo.getId() == itemId)
                return itemInfo;
        throw new Exception("Item with id " + itemId + " does not exist!");
    }

    public void disableViewHolder(int index) {
        mDataList.get(index).setEnabled(false);
    }

    public void enableViewHolder(int index) {
        mDataList.get(index).setEnabled(true);
        notifyItemChanged(index);
    }

    public boolean hasHeader() {
        return mHasHeader;
    }

    public void setHasHeader(boolean hasHeader, String label) {
        if (!mHasHeader && hasHeader) {
            mHasHeader = true;
            mDataList.add(0, new ItemInfo<>(label, ItemInfo.HEADER).setId(ItemInfo.HEADER));
            notifyDataSetChanged();
        }
    }

    public boolean hasFooter() {
        return mHasFooter;
    }

    public void setHasFooter(boolean hasFooter, String label) {
        if (!mHasFooter && hasFooter) {
            mHasFooter = true;
            int position;
            position = mDataList.size();
            mDataList.add(position, new ItemInfo<>(label, ItemInfo.FOOTER).setId(ItemInfo.FOOTER));
            notifyItemInserted(position);
        }
    }

    public void addLoading() {
        mIsLoadingFooterAdded = true;
        if (mDataList.size() > 0) {
            mDataList.add(mDataList.size() - 1, new ItemInfo<Void>(null, ItemInfo.LOADING).setId(ItemInfo.LOADING));
            notifyItemInserted(mDataList.size() - 1);
        }
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
        int position = mDataList.size() - 1;
        if (position > 0) {
            ItemInfo itemInfo;
            for (int i = 0; i < mDataList.size(); i++) {
                itemInfo = mDataList.get(i);
                if (itemInfo.getId() == ItemInfo.LOADING) {
                    mDataList.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    public boolean isSelectionAllowed() {
        return allowSelection;
    }

    public void setAllowSelection(boolean allowSelection) {
        this.allowSelection = allowSelection;
    }

    public boolean areItemsClickable() {
        return areItemsClickable;
    }

    public void setAreItemsClickable(boolean areItemsClickable) {
        this.areItemsClickable = areItemsClickable;
    }

    public boolean areItemsExpandable() {
        return areItemsExpandable;
    }

    public void setAreItemsExpandable(boolean areItemsExpandable) {
        this.areItemsExpandable = areItemsExpandable;
    }

    /**
     * Clears data from the mDataList without removing the header, footer and loading views!
     */
    public void clearPureItemList() {
        int startIndex = 0, endIndex = 0;
        if (hasHeader())
            startIndex = 1;
        if (hasFooter())
            endIndex++;
        if (mIsLoadingFooterAdded)
            endIndex++;
        for (int i = startIndex; i < mDataList.size() - endIndex; i++)
            removeItem(i);
    }

    /**
     * Clears data from the mDataList.
     */
    public void clearItemList() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    // FIXME: 17/06/16 double check!
    public void appendWithoutDuplicateIds(List<ItemInfo> itemInfoList) {
        validateList(itemInfoList);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ArraySet<ItemInfo> arraySet = new ArraySet<>();
            arraySet.addAll(itemInfoList);
            itemInfoList.clear();
            itemInfoList.addAll(arraySet);
        } else {
            Set<ItemInfo> set = new HashSet<>(itemInfoList);
            itemInfoList.clear();
            itemInfoList.addAll(set);
        }
        mDataList.addAll(itemInfoList);
        ArrayList<Long> finalList = new ArrayList<>();
        ItemInfo item;
        for (int i = 0; i < mDataList.size(); i++) {
            item = mDataList.get(i);
            if (finalList.contains(item.getId()))
                mDataList.remove(item);
            else finalList.add(item.getId());
        }
        notifyDataSetChanged();
    }

    public void appendList(List<ItemInfo> dataSet) {
        validateList(dataSet);
        mDataList.addAll(dataSet);
        notifyDataSetChanged();
    }

    public void appendList(int position, List<ItemInfo> dataSet) {
        validateList(dataSet);
        mDataList.addAll(position, dataSet);
        notifyDataSetChanged();
    }

    public boolean isSectionHeader(int index) {
        return mDataList.get(index).getId() == ItemInfo.SECTION_HEADER;
    }

    public boolean isCardSectionHeader(int index) {
        return mDataList.get(index).getId() == ItemInfo.CARD_SECTION_HEADER;
    }

    public boolean isFooter(int index) {
        return mDataList.get(index).getId() == ItemInfo.FOOTER;
    }

    public boolean isHeader(int index) {
        return mDataList.get(index).getId() == ItemInfo.HEADER;
    }

    public boolean isLoading(int index) {
        return mDataList.get(index).getId() == ItemInfo.LOADING;
    }

    public void addSectionHeader(int index, String title) {
        addItem(index, new ItemInfo<>(title, ItemInfo.SECTION_HEADER).setId(ItemInfo.SECTION_HEADER));
    }

    public void addCardSectionHeader(int index, String title) {
        addItem(index, new ItemInfo<>(title, ItemInfo.CARD_SECTION_HEADER).setId(ItemInfo.CARD_SECTION_HEADER));
    }

    public void addSectionHeaderWithId(int index, String title, long id) {
        addItem(index, new ItemInfo<>(title, ItemInfo.SECTION_HEADER).setId(id));
    }

    public void addCardSectionHeaderWithId(int index, String title, long id) {
        addItem(index, new ItemInfo<>(title, ItemInfo.CARD_SECTION_HEADER).setId(id));
    }

    public void removeSectionHeader(int index) throws Exception {
        if (mDataList.get(index).getData() instanceof String)
            removeItem(index);
        else throw new Exception("item at given index is not a section header!");
    }

    public List<ItemInfo> getPureDataList() {
        List<ItemInfo> pureSet = new ArrayList<>();
        pureSet.addAll(mDataList);
        if (hasHeader())
            pureSet.remove(0);
        if (hasFooter())
            pureSet.remove(pureSet.size() - 1);
        if (mIsLoadingFooterAdded)
            pureSet.remove(pureSet.size() - 1);
        ItemInfo item;
        for (int i = 0; i < pureSet.size(); i++) {
            item = pureSet.get(i);
            if (item.getId() == ItemInfo.SECTION_HEADER || item.getId() == ItemInfo.CARD_SECTION_HEADER
                    || item.getId() == ItemInfo.FOOTER || item.getId() == ItemInfo.HEADER
                    || item.getId() == ItemInfo.LOADING)
                pureSet.remove(item);
        }
        return pureSet;
    }

    public List<ItemInfo> getPureDataListWithSectionHeaders() {
        List<ItemInfo> pureSet = new ArrayList<>();
        pureSet.addAll(mDataList);
        if (hasHeader())
            pureSet.remove(0);
        if (hasFooter())
            pureSet.remove(pureSet.size() - 1);
        if (mIsLoadingFooterAdded)
            pureSet.remove(pureSet.size() - 1);
        return pureSet;
    }

    public List<ItemInfo> getDataList() {
        return mDataList;
    }

    public void setDataList(List<ItemInfo> dataSet) {
        validateList(dataSet);
        mDataList = dataSet;
        notifyDataSetChanged();
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) throws IllegalStateException {
        if (allowSelection)
            return getSelectedItems().contains(position);
        else throw new IllegalStateException("Selection mode is disabled!");
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public boolean toggleSelection(int position) throws IllegalStateException {
        if (allowSelection) {
            boolean isSelected;
            if (mSelectedItems.get(position, false)) {
                mSelectedItems.delete(position);
                isSelected = false;
            } else {
                mSelectedItems.put(position, true);
                isSelected = true;
            }
            notifyItemChanged(position);
            return isSelected;
        } else throw new IllegalStateException("Selection mode is disabled!");
    }

    /**
     * Set an item as selected at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public void selectItem(int position) throws IllegalStateException {
        if (allowSelection) {
            mSelectedItems.put(position, true);
            notifyItemChanged(position);
        } else throw new IllegalStateException("Selection mode is disabled!");
    }

    /**
     * Set an item as un-selected at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public void unSelectItem(int position) throws IllegalStateException {
        if (allowSelection)
            mSelectedItems.delete(position);
        else throw new IllegalStateException("Selection mode is disabled!");
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() throws IllegalStateException {
        if (allowSelection) {
            List<Integer> selection = getSelectedItems();
            mSelectedItems.clear();
            for (Integer i : selection)
                notifyItemChanged(i);
        } else throw new IllegalStateException("Selection mode is disabled!");
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    public int getSelectedItemCount() throws IllegalStateException {
        if (allowSelection)
            return mSelectedItems.size();
        else throw new IllegalStateException("Selection mode is disabled!");
    }

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items ids
     */
    public List<Integer> getSelectedItems() throws IllegalStateException {
        if (allowSelection) {
            List<Integer> items = new ArrayList<>(mSelectedItems.size());
            for (int i = 0; i < mSelectedItems.size(); ++i)
                items.add(mSelectedItems.keyAt(i));
            return items;
        } else throw new IllegalStateException("Selection mode is disabled!");
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, (lhs, rhs) -> rhs - lhs);
        // Split the list in ranges
        while (!positions.isEmpty())
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1))
                    ++count;
                if (count == 1) {
                    removeItem(positions.get(0));
                } else
                    removeRange(positions.get(count - 1), count);
                for (int i = 0; i < count; ++i)
                    positions.remove(0);
            }
    }

    private void validateList(List<ItemInfo> dataList) {
        if (dataList == null)
            throw new IllegalArgumentException("The list cannot be null");
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i)
            mDataList.remove(positionStart);
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    public <M> void animateTo(List<ItemInfo<M>> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    public void reloadData(ArrayList<ItemInfo> newModels) {
        for (ItemInfo item : mDataList)
            if (newModels.contains(item))
                newModels.remove(item);
        appendList(newModels);
    }

    //-----------------animations--------------------------//

    public ItemInfo removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataList.size());
        return mDataList.remove(position);
    }

    public void addItem(int position, ItemInfo model) {
        mDataList.add(position, model);
        notifyItemInserted(position);
        notifyItemChanged(position, mDataList.size());
    }

    public void appendItem(ItemInfo model) {
        addItem(getItemCount(), model);
    }

    public void moveItem(int fromPosition, int toPosition) {
        mDataList.add(toPosition, mDataList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    private <M> void applyAndAnimateRemovals(List<ItemInfo<M>> newModels) {
        ItemInfo model;
        for (int i = mDataList.size() - 1; i >= 0; i--) {
            model = mDataList.get(i);
            if (!newModels.contains(model))
                removeItem(i);
        }
    }

    private <M> void applyAndAnimateAdditions(List<ItemInfo<M>> newModels) {
        ItemInfo model;
        for (int i = 0, count = newModels.size(); i < count; i++) {
            model = newModels.get(i);
            if (!mDataList.contains(model))
                addItem(i, model);
        }
    }

    private <M> void applyAndAnimateMovedItems(List<ItemInfo<M>> newModels) {
        ItemInfo model;
        int fromPosition;
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            model = newModels.get(toPosition);
            fromPosition = mDataList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition)
                moveItem(fromPosition, toPosition);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, ItemInfo itemInfo, ViewHolder holder);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(int position, ItemInfo itemInfo, GenericRecyclerViewAdapter.ViewHolder holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemBase {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(Object data, boolean itemSelected, int position, boolean isEnabled) {
        }

        public void expand(boolean isExpanded) {
        }
    }
}