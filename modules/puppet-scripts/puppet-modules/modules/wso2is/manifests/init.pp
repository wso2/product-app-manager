#----------------------------------------------------------------------------
#  Copyright (c) 2015 WSO2, Inc. http://www.wso2.org
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#----------------------------------------------------------------------------
#
#
# This class installs WSO2 IS as Key Manager

class wso2is inherits wso2base {

  # Product specific Hiera lookups and overrides
  $am_datasources     = hiera_hash("wso2::am_datasources")
  $bps_datasources     = hiera_hash("wso2::bps_datasources")
  $metrics_datasources = hiera_hash("wso2::metrics_datasources")

  wso2base::server { "${carbon_home}" :
    maintenance_mode   => $maintenance_mode,
    pack_filename      => $pack_filename,
    pack_dir           => $pack_dir,
    install_mode       => $install_mode,
    install_dir        => $install_dir,
    pack_extracted_dir => $pack_extracted_dir,
    wso2_user          => $wso2_user,
    wso2_group         => $wso2_group,
    patches_dir        => $patches_dir,
    service_name       => $service_name,
    service_template   => $service_template,
    hosts_template     => $hosts_template,
    template_list      => $template_list,
    file_list          => $file_list
  }
}
