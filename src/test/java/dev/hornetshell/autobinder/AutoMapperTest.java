package dev.hornetshell.autobinder;

import com.sun.tools.javac.util.List;
import dev.hornetshell.autobinder.testdto.BigDto;
import dev.hornetshell.autobinder.testdto.SmallDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoMapperTest {


    private static Object apply(Object list) {
        return ((List) list).size();
    }

    @Test
    public void testAutoBindSimple() {
        // create a new "Big Dto"
        final BigDto bigDto = new BigDto(UUID.randomUUID(), "test", 5, "lorem ipsum", List.of("alice", "bob"));
        // create an AutoType of SmallDto
        final AutoType<SmallDto> smallDtoAutoType = AutoType.of(SmallDto.class);
        // register a converter for user count
        AutoMapper.registerGlobalConverter(SmallDto.SMALL_DTO_USER_COUNT, AutoMapperTest::apply);
        final SmallDto smallDto = AutoMapper.getAutoMapper().cast(bigDto, smallDtoAutoType);

        assertEquals(bigDto.getId(), smallDto.getId());
        assertEquals(bigDto.getName(), smallDto.getName());
        assertEquals(bigDto.getDescription(), smallDto.getDescription());
        assertEquals(bigDto.getUsers().size(), smallDto.getUserCount());
    }
}
