package com.dideng.mptt.morepycharmtkintertab

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.jetbrains.python.psi.PyArgumentList
import com.intellij.util.ProcessingContext
import com.jetbrains.python.psi.*

class TkinterCompletionProvider : CompletionProvider<CompletionParameters>() {

    // --- 数据定义 ---
    private val WINDOW_PROTOCOLS = listOf(
        "WM_DELETE_WINDOW", "WM_TAKE_FOCUS", "WM_SAVE_YOURSELF", "WM_PROTOCOL_MESSAGE"
    )

    private val XDND_EVENTS = listOf(
        "XdndEnter", "XdndPosition", "XdndDrop", "XdndStatus",
        "XdndLeave", "XdndFinished", "XdndSelection"
    )
    private val XDND_ACTIONS = listOf(
        "XdndActionCopy", "XdndActionMove", "XdndActionLink", "XdndActionAsk"
    )

    private val CLIPBOARD = listOf("CLIPBOARD", "PRIMARY", "TARGETS")

    private val CURSORS = listOf(
        "arrow", "hand2", "xterm", "crosshair", "watch",
        "size_nw_se", "size_ne_sw", "sb_h_double_arrow", "sb_v_double_arrow"
    )

    private val STATES = listOf("normal", "disabled", "readonly")
    private val ANCHORS = listOf("center", "n", "s", "e", "w", "ne", "nw", "se", "sw")
    private val EXPAND = listOf("both", "x", "y")
    private val FILL = listOf("x", "y", "both", "none")
    private val SIDE = listOf("top", "bottom", "left", "right")
    private val RELIEF = listOf("raised", "sunken", "flat", "groove", "ridge", "solid")
    private val BOOL_YESNO = listOf("yes", "no", "1", "0", "default")

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        val stringLiteral = position.parent as? PyStringLiteralExpression ?: return

        addItems(result, XDND_EVENTS + XDND_ACTIONS + CLIPBOARD, "Event/clipboard", "事件/剪贴板")
        addItems(result, SIDE + ANCHORS + FILL + EXPAND, "Layout Options", "布局选项")
        addItems(result, STATES, "Control status", "控件状态")
        addItems(result, CURSORS, "Cursor style", "光标样式")
        addItems(result, WINDOW_PROTOCOLS, "Window Protocol", "窗口协议")
    }

    // --- 上下文判断逻辑 ---

    /**
     * 判断是否在 root.protocol(...) 调用中，且当前字符串是第一个参数
     */
    private fun isInsideProtocolCall(literal: PyStringLiteralExpression): Boolean {
        val argumentList = literal.parent as? PyArgumentList ?: return false
        val callExpression = argumentList.parent as? PyCallExpression ?: return false
        val callee = callExpression.callee ?: return false
        if (callee.text != "protocol") return false
        val arguments = callExpression.arguments
        return arguments.isNotEmpty() && arguments[0] == literal
    }

    // --- 核心：显示中英双语 ---
    /**
     * 向结果集中添加补全项，并显示中英双语提示
     */
    private fun addItems(
        result: CompletionResultSet,
        items: List<String>,
        englishDesc: String,
        chineseDesc: String
    ) {
        val combinedText = "($englishDesc / $chineseDesc)"
        for (item in items) {
            result.addElement(
                LookupElementBuilder.create(item)
                    .bold()
                    .withTailText(combinedText, true)
            )
        }
    }
}