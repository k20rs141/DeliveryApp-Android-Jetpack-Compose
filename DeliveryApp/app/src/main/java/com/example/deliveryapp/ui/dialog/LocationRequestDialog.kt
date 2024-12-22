package com.example.deliveryapp.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun LocationRequestDialog(
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("注意！！")
        },
        text = {
            Text(
                "トラック管理のため，位置情報を「常に許可」に設定してください。" +
                        "\n 「アプリ情報」→「権限」→「位置情報」→「常に許可」"
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick
            ) {
                Text("更新")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("キャンセル")
            }
        }
    )
}
