package com.xforceplus.wapp.modules.rednotification.mapstruct;

import com.xforceplus.wapp.sequence.IDSequence;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@UtilityClass
public class IdGenerator {
    @Setter(AccessLevel.PRIVATE)
    IDSequence iDSequence;

    public static Long generate() {
        return iDSequence.nextId();
    }

    @Component
    private static class Setup {
        @Autowired
        private void setIDGenerator(IDSequence idGenerator) {
            IdGenerator.setIDSequence(idGenerator);
        }
    }
}