package com.tschuchort.compiletesting

import org.jetbrains.kotlin.ksp.processing.CodeGenerator
import org.jetbrains.kotlin.ksp.processing.Resolver
import org.jetbrains.kotlin.ksp.processing.SymbolProcessor
import org.junit.rules.TestWatcher
import org.junit.runner.Description

internal class DelegateProcessor : SymbolProcessor {
    private val delegate
        get() = checkNotNull(Companion.delegate) {
            "Delegate is not provided"
        }

    override fun finish() {
        delegate.finish()
    }

    override fun init(options: Map<String, String>, kotlinVersion: KotlinVersion, codeGenerator: CodeGenerator) {
        delegate.init(
            options = options,
            kotlinVersion = kotlinVersion,
            codeGenerator = codeGenerator
        )
    }

    override fun process(resolver: Resolver) {
        delegate.process(resolver)
    }

    companion object {
        var delegate: SymbolProcessor? = null
    }
}

class DelegatingSymbolProcessorRule : TestWatcher() {
    fun delegateTo(processor: SymbolProcessor): Class<out SymbolProcessor> {
        check(DelegateProcessor.delegate == null) {
            "Cannot use DelegatingSymbolProcessorRule more than once"
        }
        DelegateProcessor.delegate = processor
        return DelegateProcessor::class.java
    }

    override fun finished(description: Description?) {
        DelegateProcessor.delegate = null
        super.finished(description)
    }
}