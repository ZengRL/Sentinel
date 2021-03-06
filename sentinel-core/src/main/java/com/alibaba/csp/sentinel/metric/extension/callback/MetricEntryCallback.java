package com.alibaba.csp.sentinel.metric.extension.callback;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.metric.extension.AdvancedMetricExtension;
import com.alibaba.csp.sentinel.metric.extension.MetricExtension;
import com.alibaba.csp.sentinel.metric.extension.MetricExtensionProvider;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotEntryCallback;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * Metric extension entry callback.
 *
 * @author Carpenter Lee
 * @since 1.6.1
 */
public class MetricEntryCallback implements ProcessorSlotEntryCallback<DefaultNode> {
	@Override
	public void onPass(Context context, ResourceWrapper resourceWrapper, DefaultNode param, int count, Object... args)
			throws Exception {
		for (MetricExtension m : MetricExtensionProvider.getMetricExtensions()) {
			if (m instanceof AdvancedMetricExtension) {
				((AdvancedMetricExtension) m).increaseThreadNum(resourceWrapper.getName(),
						resourceWrapper.getEntryType().name(), args);
				((AdvancedMetricExtension) m).addPass(resourceWrapper.getName(), resourceWrapper.getEntryType().name(),
						count, args);
			} else {
				m.increaseThreadNum(resourceWrapper.getName(), args);
				m.addPass(resourceWrapper.getName(), count, args);
			}
		}
	}

	@Override
	public void onBlocked(BlockException ex, Context context, ResourceWrapper resourceWrapper, DefaultNode param,
			int count, Object... args) {
		for (MetricExtension m : MetricExtensionProvider.getMetricExtensions()) {
			if (m instanceof AdvancedMetricExtension) {
				((AdvancedMetricExtension) m).addBlock(resourceWrapper.getName(), resourceWrapper.getEntryType().name(),
						count, context.getOrigin(), ex, args);
			} else {
				m.addBlock(resourceWrapper.getName(), count, context.getOrigin(), ex, args);
			}
		}
	}
}
