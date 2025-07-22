package com.dideng.mptt.morepycharmtkintertab

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.jetbrains.python.psi.PyStringLiteralExpression

class TkinterCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inside(PyStringLiteralExpression::class.java),
            TkinterCompletionProvider()
        )
    }
}