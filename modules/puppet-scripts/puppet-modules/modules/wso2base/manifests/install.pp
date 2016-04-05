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

define wso2base::install ($mode, $install_dir, $pack_filename, $pack_dir, $user, $group, $product_name) {
  $carbon_home = $name
  $pack_file_abs_path = "${pack_dir}/${pack_filename}"

  notice("Installing WSO2 product [name] ${::product_name}, [version] ${::product_version}, [CARBON_HOME] ${carbon_home}")

  # create directories for installation if they do not exist
  ensure_resource('file', [$install_dir, $pack_dir], { ensure => 'directory' })

  # download wso2 product pack archive
  case $mode {
    'file_repo': {
      $remote_file_url = hiera("remote_file_url")

      ensure_resource('exec', "$pack_file_abs_path", {
        path           => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
        cwd            => $pack_dir,
        command        => "wget -q ${remote_file_url}",
        logoutput      => 'on_failure',
        creates        => $pack_file_abs_path,
        onlyif         => "test -f $pack_file_abs_path",
        timeout        => 0,
        notify         => Exec["extract_${pack_file_abs_path}"],
        require        => File["${install_dir}", "${pack_dir}"]
      })
    }

    'file_bucket' : {
      ensure_resource('file', "$pack_file_abs_path", {
        mode           => 750,
        owner          => $user,
        group          => $group,
        source         => ["puppet:///modules/${product_name}/${pack_filename}", "puppet:///files/packs/${pack_filename}"],
        notify         => Exec["extract_${pack_file_abs_path}"],
        require        => File["${install_dir}", "${pack_dir}"]
      })
    }
  }

  # extract downloaded wso2 product pack archive
  ensure_resource('exec', "extract_${pack_file_abs_path}", {
    path               => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
    cwd                => $install_dir,
    command            => "unzip ${pack_file_abs_path}",
    logoutput          => 'on_failure',
    creates            => "${carbon_home}/bin",
    timeout            => 0,
    refreshonly        => true,
    notify             => Exec["set_ownership_${carbon_home}"]
  })

  # set ownership for carbon_home
  ensure_resource('exec', "set_ownership_${carbon_home}", {
    path               => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
    cwd                => $carbon_home,
    command            => "chown -R ${user}:${group} ./",
    logoutput          => 'on_failure',
    timeout            => 0,
    refreshonly        => true,
    notify             => Exec["set_permissions_${carbon_home}"]
  })

  # set file permissions for carbon_home
  ensure_resource('exec', "set_permissions_${carbon_home}", {
    path               => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
    cwd                => $carbon_home,
    command            => "chmod -R 754 ./",
    logoutput          => 'on_failure',
    timeout            => 0,
    refreshonly        => true,
    notify             => Exec["remove_product_pack"]
  })


  # Remove wso2 product pack archive
  exec { "remove_product_pack":
    path               => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
    command            => "rm -rf ${pack_file_abs_path}",
  }


}
