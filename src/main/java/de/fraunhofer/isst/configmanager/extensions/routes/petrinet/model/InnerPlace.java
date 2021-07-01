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
package de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.Objects;

/**
 * Used for inner places of unfolded transitions (has a originalTrans field to access the original transition which
 * was unfolded).
 */
@Getter
@Setter
public class InnerPlace extends PlaceImpl {
    /**
     * Original Transition, which was unfolded to create the InnerPlace.
     */
    private Transition originalTrans;

    public InnerPlace(final URI id, final Transition originalTrans) {
        super(id);
        this.originalTrans = originalTrans;
    }

    @Override
    public Node deepCopy() {
        final var copy = new InnerPlace(this.getID(), this.originalTrans);
        copy.setMarkers(this.getMarkers());
        return copy;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var place = (InnerPlace) o;

        return originalTrans.equals(place.originalTrans) && getMarkers() == place.getMarkers() && Objects.equals(getID(), place.getID());
    }
}
