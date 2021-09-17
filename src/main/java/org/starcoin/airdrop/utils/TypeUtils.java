package org.starcoin.airdrop.utils;


import org.starcoin.bean.TypeObj;
import org.starcoin.types.TypeTag;

public class TypeUtils {

    public static TypeTag parseTypeTag(String s) {
        String[] fs = s.split("::");
        TypeObj typeObj = TypeObj.builder().moduleAddress(fs[0]).moduleName(fs[1]).name(fs[2]).build();
        return typeObj.toTypeTag();
    }
}
