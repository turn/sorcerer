/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.config.impl;

import com.turn.sorcerer.module.ModuleType;
import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.StatusStorage;
import com.turn.sorcerer.status.impl.HDFSStatusStorage;
import com.turn.sorcerer.status.impl.ZookeeperStatusStorage;
import com.turn.sorcerer.status.type.impl.HDFSStatusStorageType;
import com.turn.sorcerer.status.type.impl.ZookeeperStatusStorageType;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.email.EmailType;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Sorcerer Guice abstract module
 *
 * @author tshiou
 */
public class SorcererAbstractModule extends AbstractModule {

	private Map<String, TaskType> tasks = Maps.newHashMap();
	private Map<String, Class<? extends Task>> taskClasses = Maps.newHashMap();
	private Map<String, PipelineType> pipelines = Maps.newHashMap();
	private Map<String, Class<? extends Pipeline>> pipelineClasses = Maps.newHashMap();
	private ModuleType module;

	protected SorcererAbstractModule(
			Map<String, TaskType> tasks,
			Map<String, Class<? extends Task>> taskClasses,
			Map<String, PipelineType> pipelines,
			Map<String, Class<? extends Pipeline>> pipelineClasses,
			ModuleType module
	) {
		this.tasks = tasks;
		this.taskClasses = taskClasses;
		this.pipelines = pipelines;
		this.pipelineClasses = pipelineClasses;
		this.module = module;
	}

	@Override
	protected void configure() {
		// Task

		// Bind task types annotated by their name
		for (Map.Entry<String, TaskType> entry : tasks.entrySet()) {
			bind(TaskType.class).annotatedWith(Names.named(entry.getKey()))
					.toInstance(entry.getValue());
		}

		// Bind task classes
		for (Map.Entry<String, Class<? extends Task>> entry : taskClasses.entrySet()) {
			bind(Task.class).annotatedWith(Names.named(entry.getKey()))
					.to(entry.getValue());
		}



		for (Map.Entry<String, PipelineType> entry : pipelines.entrySet()) {
			// Bind pipeline type
			bind(PipelineType.class).annotatedWith(Names.named(entry.getKey()))
					.toInstance(entry.getValue());
		}

		// Bind pipeline classes
		for (Map.Entry<String, Class<? extends Pipeline>> entry : pipelineClasses.entrySet()) {
			bind(Pipeline.class).annotatedWith(Names.named(entry.getKey()))
					.to(entry.getValue());
		}

		// Bind module and configurations
		bind(ModuleType.class).toInstance(module);

		// Pipelines to run
		Multibinder<PipelineType> pipelineSet = Multibinder.newSetBinder(binder(), PipelineType.class);
		for (String pipelineName : module.getPipelines()) {
			pipelineSet.addBinding().toInstance(pipelines.get(pipelineName));
		}

		bind(EmailType.class).toInstance(module.getAdminEmail());

		bind(StatusStorage.class).to(module.getStorage().getStorageClass());

		if (module.getStorage().getClass() == HDFSStatusStorageType.class) {
			bind(String.class).annotatedWith(HDFSStatusStorage.HDFSStorageRoot.class)
					.toInstance(((HDFSStatusStorageType) module.getStorage()).getRoot());
		} else if (module.getStorage().getClass() == ZookeeperStatusStorageType.class) {
			bindZookeeperStorage();
		}
	}

	private void bindZookeeperStorage() {
		bind(String.class).annotatedWith(ZookeeperStatusStorage.StorageRoot.class)
				.toInstance(((ZookeeperStatusStorageType) module.getStorage()).getRoot());
		bind(String.class).annotatedWith(ZookeeperStatusStorage.ConnectionString.class)
				.toInstance(((ZookeeperStatusStorageType) module.getStorage()).getConnectionString());
		bind(Integer.class).annotatedWith(Names.named(ZookeeperStatusStorage.SESSION_TIMEOUT))
				.toInstance(((ZookeeperStatusStorageType) module.getStorage()).getSessionTimeout());
		bind(Integer.class).annotatedWith(Names.named(ZookeeperStatusStorage.CONNECTION_TIMEOUT))
				.toInstance(((ZookeeperStatusStorageType) module.getStorage()).getConnectionTimeout());
		bind(Integer.class).annotatedWith(Names.named(ZookeeperStatusStorage.RETRY_DURATION))
				.toInstance(((ZookeeperStatusStorageType) module.getStorage()).getRetryDuration());
		bind(Integer.class).annotatedWith(Names.named(ZookeeperStatusStorage.RETRY_INTERVAL))
				.toInstance(((ZookeeperStatusStorageType) module.getStorage()).getRetryInterval());
	}

}
