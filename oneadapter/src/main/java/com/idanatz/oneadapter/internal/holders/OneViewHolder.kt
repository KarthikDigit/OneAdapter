package com.idanatz.oneadapter.internal.holders

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.idanatz.oneadapter.external.events.ClickEventHook
import com.idanatz.oneadapter.external.events.EventHook
import com.idanatz.oneadapter.external.events.EventHooksMap
import com.idanatz.oneadapter.internal.selection.OneItemDetail
import com.idanatz.oneadapter.internal.interfaces.InternalModuleConfig
import com.idanatz.oneadapter.external.states.StatesMap
import com.idanatz.oneadapter.internal.utils.let2

@Suppress("NAME_SHADOWING")
internal abstract class OneViewHolder<M> (
        parent: ViewGroup,
        moduleConfig: InternalModuleConfig,
        private val statesMap: StatesMap<M>? = null,
        private val eventsMap: EventHooksMap<M>? = null
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(moduleConfig.withLayoutResource(), parent, false)) {

    private var model: M? = null
    lateinit var viewBinder: ViewBinder

    abstract fun onBind(model: M?)
    abstract fun onUnbind()

    fun onBindViewHolder(model: M?) {
        this.model = model
        this.viewBinder = ViewBinder(itemView)

        handleEventHooks()
        onBind(model)
    }

    private fun handleEventHooks() {
        let2(eventsMap?.getClickEventHook(), model) { clickEventHook, model ->
            itemView.setOnClickListener { clickEventHook.onClick(model, viewBinder) }
        }
    }

    fun onBindSelection(model: M?, selected: Boolean) {
        let2(statesMap?.getSelectionState(), model) { selectionState, model ->
            if (selected) selectionState.onSelected(model, true)
            else selectionState.onSelected(model, false)
        }
    }

    fun createItemLookupInformation(): OneItemDetail? {
        var result: OneItemDetail? = null
        let2(statesMap?.getSelectionState(), model) { selectionState, model ->
            if (selectionState.selectionEnabled(model)) {
                result = OneItemDetail(adapterPosition, itemId)
            }
        }
        return result
    }
}