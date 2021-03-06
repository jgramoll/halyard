/*
 * Copyright 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.services.v1.ArtifactService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class ShutdownScriptProfileFactoryBuilder {
  @Autowired protected ArtifactService artifactService;

  @Autowired protected Yaml yamlParser;

  @Autowired protected ObjectMapper objectMapper;

  public ProfileFactory build(String shutdownCommands, SpinnakerArtifact artifact) {
    return new ProfileFactory() {
      @Override
      protected ArtifactService getArtifactService() {
        return artifactService;
      }

      @Override
      protected void setProfile(
          Profile profile,
          DeploymentConfiguration deploymentConfiguration,
          SpinnakerRuntimeSettings endpoints) {
        profile.appendContents(shutdownCommands);
        profile.setExecutable(true);
      }

      @Override
      protected Profile getBaseProfile(String name, String version, String outputFile) {
        return new Profile(name, version, outputFile, "#!/usr/bin/env bash\n\n");
      }

      @Override
      public SpinnakerArtifact getArtifact() {
        return artifact;
      }

      @Override
      protected String commentPrefix() {
        return "## ";
      }
    };
  }
}
