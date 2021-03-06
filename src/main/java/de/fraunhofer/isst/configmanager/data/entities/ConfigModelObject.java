/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fraunhofer.isst.configmanager.data.entities;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.isst.configmanager.data.util.ConfigModelConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity class for the configuration model.
 */
@Data
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Convert(converter = ConfigModelConverter.class)
    @Column(columnDefinition = "TEXT")
    ConfigurationModel configurationModel;

    public ConfigModelObject(final ConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }
}
