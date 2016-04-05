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
# This class installs WSO2 Business Process Server

class wso2bps inherits wso2base {

  $jvm                                        = hiera("wso2::jvm")
  $so_timeout                                 = hiera("wso2::so_timeout")
  $connection_timeout                         = hiera("wso2::connection_timeout")
  $mex_timeout                                = hiera("wso2::mex_timeout")
  $external_service_timeout                   = hiera("wso2::external_service_timeout")
  $max_connections_per_host                   = hiera("wso2::max_connections_per_host")
  $max_total_connections                      = hiera("wso2::max_total_connections")
  $ode_scheduler_thread_pool_size             = hiera("wso2::ode_scheduler_thread_pool_size")
  $scheduler_config_max_thread_pool_size      = hiera("wso2::scheduler_config_max_thread_pool_size")
  $enable_humantask_caching                   = hiera("wso2::enable_humantask_caching")
  $activiti_datasources                       = hiera("wso2::activiti_datasources")
  $bps_datasources                            = hiera("wso2::bps_datasources")

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