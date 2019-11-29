package ssu.ssu.huncheckwhatssu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.util.Vector;

import ssu.ssu.huncheckwhatssu.utilClass.Customer;
import ssu.ssu.huncheckwhatssu.utilClass.Trade;

public class RecyclerViewTradeAdapter_Sell extends RecyclerView.Adapter<RecyclerViewTradeAdapter_Sell.TradeViewHolder> {
    LayoutInflater inflater;
    Vector<Trade> modelVector;
    RecyclerView recyclerView;

    public RecyclerViewTradeAdapter_Sell(Context context,  Vector<Trade> vector, RecyclerView recyclerView) {
        inflater = LayoutInflater.from(context);
        modelVector = vector;
        this.recyclerView = recyclerView;
    }

    @Override
    public TradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.trade_item_fragment_sell, parent, false);
        return new TradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TradeViewHolder holder, int position) {
        holder.bindData(modelVector.get(position));
    }

    public Vector<Trade> getTrades(){ return modelVector;}

    @Override
    public int getItemCount() {
        return modelVector.size();
    }

    class TradeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView bookTitleTextView;
        TextView sellerNameTextView;
        TextView originalPriceTextView;
        TextView sellingPriceTextView;
        TextView bookCategoryTextView;
        TextView bookAuthorTextView;
        TextView bookPublisherTextView;
        TextView sellerCreditTextView;

        public TradeViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            bookTitleTextView = itemView.findViewById(R.id.item_book_title);
            sellerNameTextView = itemView.findViewById(R.id.item_seller_name);
            originalPriceTextView = itemView.findViewById(R.id.item_book_original_price);
            sellingPriceTextView =itemView.findViewById(R.id.item_book_selling_price);
            bookCategoryTextView = itemView.findViewById(R.id.item_book_category);
            bookAuthorTextView = itemView.findViewById(R.id.item_book_author);
            bookPublisherTextView = itemView.findViewById(R.id.item_book_publisher);
            sellerCreditTextView = itemView.findViewById(R.id.item_seller_credit);
        }

        public void bindData(Trade object) {
            if(object.getSeller().getName() == null) {
                object.setSeller(new Customer(object.getSellerId()));
                object.getSeller().setCustomerDataFromUID(recyclerView.getAdapter());
            }
            imageView.setBackgroundResource(R.drawable.bookimag);
            bookTitleTextView.setText(object.getBook().getTitle());
            sellerNameTextView.setText(object.getSeller().getName());
            originalPriceTextView.setText(String.valueOf(object.getBook().getOriginalPrice()));
            sellingPriceTextView.setText(String.valueOf(object.getSellingPrice()));
            bookAuthorTextView.setText(object.getBook().getAuthor());
            bookPublisherTextView.setText(object.getBook().getPublisher());
            sellerCreditTextView.setText("위험");
        }
    }

    //RecyclerView에 TouchListener 설정 함수 (Swipe로 메뉴 출력 가능하게)
    public static void setSwipeable(final Context context, final Activity activity, final Fragment fragment, final RecyclerView recyclerView) {
        RecyclerTouchListener onTouchListener = new RecyclerTouchListener(activity, recyclerView);
        onTouchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Trade trade = ((RecyclerViewTradeAdapter_Sell)(recyclerView.getAdapter())).getTrades().get(position);
                        Toast toast = Toast.makeText(context, "RowClick! " + trade.getBook().getTitle(), Toast.LENGTH_SHORT);
                        toast.show();
                        recyclerView.getAdapter().notifyItemChanged(position);
                        Intent intent=new Intent(context,BookInfoActivity.class);
                        intent.putExtra("BookInfoType","BOOK_INFO_TRADE_DETAIL");
                        intent.putExtra("book_info_trade_detail", trade);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        Toast toast = Toast.makeText(context, "IndependentViewID", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setSwipeOptionViews(R.id.item_button_edit, R.id.item_button_delete)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {

                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        Trade trade = ((RecyclerViewTradeAdapter_Sell)(recyclerView.getAdapter())).getTrades().get(position);
                        if (viewID == R.id.item_button_edit) {
                            Toast toast = Toast.makeText(context, "Edit! " + trade.getBook().getTitle(), Toast.LENGTH_SHORT);
                            toast.show();

                            Intent intent=new Intent(context, EditSell.class);
                            intent.putExtra("BookInfoType","BOOK_INFO_SELL_EDIT_DETAIL");
                            intent.putExtra("book_info_sell_edit_detail", trade);
                            intent.putExtra("position", position);
                            fragment.startActivityForResult(intent, 0);

                            recyclerView.getAdapter().notifyItemChanged(position);
                        } else if (viewID == R.id.item_button_delete) {
                            Toast toast = Toast.makeText(context, "Delete! " + trade.getBook().getTitle(), Toast.LENGTH_SHORT);
                            toast.show();
                            if(trade.getTradeState()== Trade.TradeState.WAIT){
                                /*판매 등록 삭제*/
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("판매 종료");
                                alert.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        /*삭제되는 코드 넣기*///  recyclerView.remove(position);
                                        Toast toast=Toast.makeText(context,"판매종료",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                                alert.setNegativeButton("유지", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Toast toast=Toast.makeText(context,"유지",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                                alert.show();
                            }
                            else if(trade.getTradeState()==Trade.TradeState.COMPLETE){
                                /*만약 상태가 거래완료이면
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("거래 내역 삭제");
                                alert.setMessage("삭제시, 거래 내역을 볼 수 없습니다.");
                                alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                         recyclerView.remove(position);
                                        Toast toast=Toast.makeText(context,"내역삭제함",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Toast toast=Toast.makeText(context,"취소함",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                                alert.show();
                                */
                            }
                            else if(trade.getTradeState()== Trade.TradeState.PRECONTRACT){
                                /*만약, 상태가 거래진행중이면*/
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("거래 취소");
                                alert.setMessage("정말로 거래 취소 하시겠습니까?\n 신용도에 영향을 줍니다.");
                                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        /*삭제되는 코드 넣기*///  recyclerView.remove(position);
                                        Toast toast=Toast.makeText(context,"거래취소함",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Toast toast=Toast.makeText(context,"취소함",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                                alert.show();
                            }

                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }
                });
        recyclerView.addOnItemTouchListener(onTouchListener);
        return;
    }

}