package dev.hornetshell.autobinder;

import dev.hornetshell.autobinder.testdto.BigDto;
import dev.hornetshell.autobinder.testdto.SmallDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoMapperTest {


    private static Integer applyUserCount(Object list) {
        return ((List) list).size();
    }

    private static List<String> applyPermissions(Object list) {
        return ((List<BigDto.Permission>) list)
                .stream()
                .filter(permission -> permission.isAllowed())
                .map(permission -> permission.getName())
                .collect(Collectors.toList());
    }

    @Test
    public void testAutoBindSimple() {
        // create a new "Big Dto"
        final BigDto bigDto = new BigDto(UUID.randomUUID(), "test", 5,
                "lorem ipsum", Arrays.asList("alice", "bob"),
                Arrays.asList(
                        new BigDto.Permission("login", true), new BigDto.Permission("write", false)
                )
        );
        // create an AutoType of SmallDto
        final AutoType<SmallDto> smallDtoAutoType = AutoType.of(SmallDto.class);
        // register a converter for user count
        AutoMapper.registerGlobalConverter(SmallDto.SMALL_DTO_USER_COUNT, AutoMapperTest::applyUserCount);
        AutoMapper.registerGlobalConverter(SmallDto.SMALL_DTO_PERMISSIONS, AutoMapperTest::applyPermissions);
        final SmallDto smallDto = AutoMapper.getAutoMapper().cast(bigDto, smallDtoAutoType);

        assertEquals(bigDto.getId(), smallDto.getId());
        assertEquals(bigDto.getName(), smallDto.getName());
        assertEquals(bigDto.getDescription(), smallDto.getDescription());
        assertEquals(bigDto.getUsers().size(), smallDto.getUserCount());
        assertArrayEquals(new String[] {"login"}, smallDto.getPermissions().toArray(new String[0]));
    }
}
