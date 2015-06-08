/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.config.impl;

import com.turn.sorcerer.config.ConfigReader;
import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.module.ModuleType;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.type.impl.HDFSStatusStorageType;
import com.turn.sorcerer.status.type.impl.MemoryStatusStorageType;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.email.EmailType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class YamlConfigReader implements ConfigReader {

	private static final Logger logger =
			LoggerFactory.getLogger(YamlConfigReader.class);

	// YamlReader configuration
	private static final String TASK_TAG = "task";
	private static final String PIPELINE_TAG = "pipeline";
	private static final String MODULE_TAG = "module";
	private static final String HDFS_STATUS_TAG = "hdfs";
	private static final String MEMORY_STATUS_TAG = "memory";
	private static final String EMAIL_TAG = "email";

	private static final YamlConfig YAML_CONFIG = new YamlConfig();
	static {
		YAML_CONFIG.setClassTag(TASK_TAG, TaskType.class);
		YAML_CONFIG.setClassTag(PIPELINE_TAG, PipelineType.class);
		YAML_CONFIG.setClassTag(MODULE_TAG, ModuleType.class);
		YAML_CONFIG.setClassTag(HDFS_STATUS_TAG, HDFSStatusStorageType.class);
		YAML_CONFIG.setClassTag(MEMORY_STATUS_TAG, MemoryStatusStorageType.class);
		YAML_CONFIG.setClassTag(EMAIL_TAG, EmailType.class);
		YAML_CONFIG.setPrivateFields(true);
	}

	// Yaml filetype filter
	private static final FilenameFilter YAML_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".yaml") || name.endsWith(".yml");
		}
	};

	@Override
	public Collection<String> read(Collection<File> paths) throws SorcererException {
		List<String> packages = Lists.newArrayList();

		for (File f : paths) {

			List<File> files = Lists.newArrayList();

			if (f.isFile()) {
				files.add(f);
			}

			if (f.isDirectory()) {
				Collections.addAll(files, f.listFiles(YAML_FILTER));
			}

			for (File file : files) {

				Reader fReader;
				try {
					fReader = new FileReader(file);
				} catch (FileNotFoundException e) {
					logger.error("Could not find config file " + file);
					continue;
				}

				YamlReader reader = new YamlReader(fReader, YAML_CONFIG);

				try {
					while (true) {
						Object obj;
						try {
							obj = reader.read();
						} catch (YamlException e) {
							logger.error("Error while reading yaml configuration file " + file.getName(), e);
							continue;
						}

						if (obj == null) {
							break;
						}

						if (obj instanceof TaskType) {
							SorcererRegistry.get().registerTask((TaskType) obj);
						} else if (obj instanceof PipelineType) {
							SorcererRegistry.get().registerPipeline((PipelineType) obj);
						} else if (obj instanceof ModuleType) {
							SorcererRegistry.get().registerModule((ModuleType) obj);
							if (((ModuleType) obj).getPackages() != null){
								packages.addAll(((ModuleType) obj).getPackages());
							}
						} else {
							continue;
						}


					}
				} finally {
					try {
						fReader.close();
						reader.close();
					} catch (IOException e) {
						logger.debug("Couldn't close readers", e);
					}

				}
			}
		}

		return packages;
	}

	@Override
	public FilenameFilter getFileFilter() {
		return YAML_FILTER;
	}
}
