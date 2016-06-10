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

define wso2base::clean ($mode, $pack_filename, $pack_dir) {
  notice("Cleaning WSO2 product [name] ${::product_name}, [version] ${::product_version}, [mode] ${mode}")
  if $mode == 'refresh' {
    exec {
      "Remove_lock_file_${name}":
        path    => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
        onlyif  => "test -f ${name}/wso2carbon.lck",
        command => "rm ${name}/wso2carbon.lck";

      "Stop_process_${name}":
        path    => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/java/bin/',
        command => "kill -9 `cat ${name}/wso2carbon.pid`; /bin/echo Killed",
        require => Exec["Remove_lock_file_${name}"];
    }
  } elsif $mode == 'new' {
    exec { "Stop_process_and_remove_CARBON_HOME_${name}":
      path    => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/java/bin/',
      command => "kill -9 `cat ${name}/wso2carbon.pid`; rm -rf ${name}";
    }
  } elsif $mode == 'zero' {
    exec { "Stop_process_remove_CARBON_HOME_and_pack_${name}":
      path    => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/java/bin/',
      command => "kill -9 `cat ${name}/wso2carbon.pid`; rm -rf ${name}; rm -f ${pack_dir}/${pack_filename}";
    }
  }
}
