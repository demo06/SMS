package com.lx.sms.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashMap;

/**
 * dialog工具类
 */

public class DialogUtil {
    private static HashMap<String, Dialog> dialogMap = new HashMap<>();


    private static void filterDialog(String dialogTag) {
        Dialog dialog = dialogMap.get(dialogTag);
        if (dialog != null) {
            dismissDialog(dialogTag);
        }
    }

    /**
     * 关闭对话框
     *
     * @param dialogTag
     */
    public static void dismissDialog(String dialogTag) {
        Dialog dialog = dialogMap.get(dialogTag);
        if (dialog != null) {
            dialog.dismiss();
            removeDialog(dialogTag);
        }
    }

    public static void removeDialog(String dialogTag) {
        dialogMap.remove(dialogTag);
    }


    /**
     * 创建选择日期的对话框
     */
    public static void showDatePickerDialog(Context context, String dialogTag, String title
            , final OnDatePickerDialogConfirmListener listener) {
        filterDialog(dialogTag);
        final DatePicker datePicker = new DatePicker(context);
        datePicker.setCalendarViewShown(false);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(datePicker)
                .setPositiveButton("确定", (dialog, which) -> {
                    if (listener != null) {
                        listener.onDatePickerComfirm(datePicker.getYear()
                                , datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                    }
                })
                .create();
        alertDialog.show();
        dialogMap.put(dialogTag, alertDialog);
    }

    /**
     * 创建选择时间的对话框
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void showTimerPickerDialog(Context context, String dialogTag, String title
            , final OnTimerPickerDialogConfirmListener listener) {
        filterDialog(dialogTag);
        final TimePicker timePicker = new TimePicker(context);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(timePicker)
                .setPositiveButton("确定", (dialog, which) -> {
                    if (listener != null) {
                        listener.onTimerPickerComfirm(timePicker.getHour()
                                , timePicker.getMinute());
                    }
                })
                .create();
        alertDialog.show();
        dialogMap.put(dialogTag, alertDialog);
    }


    public interface OnDatePickerDialogConfirmListener {
        void onDatePickerComfirm(int year, int month, int day);
    }

    public interface OnTimerPickerDialogConfirmListener {
        void onTimerPickerComfirm(int hourOfDay, int minute);
    }


    /***
     * 获取一个dialog
     * @param context
     * @return
     */
    public static AlertDialog.Builder getDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder;
    }

    /***
     * 获取一个耗时等待对话框
     * @param context
     * @param message
     * @return
     */
    public static ProgressDialog getWaitDialog(Context context, String message) {
        ProgressDialog waitDialog = new ProgressDialog(context);
        if (!TextUtils.isEmpty(message)) {
            waitDialog.setMessage(message);
        }
        return waitDialog;
    }

    /***
     * 获取一个信息对话框，注意需要自己手动调用show方法显示
     * @param context
     * @param message
     * @param onClickListener
     * @return
     */
    public static AlertDialog.Builder getMessageDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton("确定", onClickListener);
        return builder;
    }

    /**
     * 显示列表选择的对话框
     *
     * @param context
     * @param items
     * @param listener
     */
    public static void showListDialog(Context context, String title, String[] items, MaterialDialog.ListCallback listener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.items(items);
        builder.itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                listener.onSelection(dialog, itemView, position, text);
            }
        });
        builder.show();
    }

    /**
     * 显示普通确定取消对话框
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showDialog(Context context, String title, String content, final OnShowDialogConfirmListener listener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.content(content);
        builder.positiveText("确定");
        builder.negativeText("取消");
        builder.onPositive((dialog, which) -> {
                    listener.doSomething();
                    dialog.dismiss();
                }
        );
        builder.onNegative((dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * 显示输入框的对话框
     *
     * @param context
     */
    public static void showEditDialog(Context context, String dialogTag, String title, final OnEditDialogConfirmListener
            listener) {
        filterDialog(dialogTag);
        final EditText editText = new EditText(context);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("确定", (dialog, which) -> {
                    if (listener != null && !TextUtils.isEmpty(editText.getText().toString())) {
                        listener.onEditDialogConfirm(editText.getText().toString());
                    } else {
                        listener.onTextIsEmpty();
                    }
                })
                .create();
        alertDialog.show();
        dialogMap.put(dialogTag, alertDialog);
    }

    /**
     * 显示设置对话框
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showSignDialog(Context context, String title, String btnText, String content, final OnShowDialogConfirmListener listener, final DialogInterface.OnKeyListener keyListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.content(content);
        builder.positiveText(btnText);
        builder.keyListener(keyListener);
        builder.onPositive((dialog, which) -> {
                    listener.doSomething();
                    dialog.dismiss();
                }
        );
        builder.show();
    }

    public static void showDialog(Context context, String title, String content, final OnShowDialogConfirmListener listener, final OnShowDialogCancelListener cancelListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.content(content);
        builder.positiveText("确定");
        builder.negativeText("取消");
        builder.onPositive((dialog, which) ->
                listener.doSomething()

        );
        builder.onNegative((dialog, which) -> cancelListener.doSomething());
        builder.show();
    }

    public interface OnShowDialogConfirmListener {
        void doSomething();
    }

    public interface OnShowDialogCancelListener {
        void doSomething();
    }

    public interface OnEditDialogConfirmListener {
        void onEditDialogConfirm(String content);

        void onTextIsEmpty();
    }


}
