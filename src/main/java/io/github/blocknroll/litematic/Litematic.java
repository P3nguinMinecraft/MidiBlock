package io.github.blocknroll.litematic;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.selection.AreaSelection;

public class Litematic {

    public LitematicaSchematic fromArea(AreaSelection area) {
        return LitematicaSchematic.createEmptySchematic(area, "blocknroll");
    }


}
