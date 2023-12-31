package edu.unh.cs.cs619.bulletzone.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;


import javax.servlet.http.HttpServletRequest;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Player;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.exceptions.TokenDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.InventoryWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.util.PlayerWrapper;
import edu.unh.cs.cs619.bulletzone.util.StringArrayWrapper;
import jdk.internal.org.jline.utils.Log;

@RestController
@RequestMapping(value = "/games")
class GamesController {

    private static final Logger log = LoggerFactory.getLogger(GamesController.class);

    private final GameRepository gameRepository;

    @Autowired
    public GamesController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    ResponseEntity<PlayerWrapper> join(HttpServletRequest request, @PathVariable int id) {
        Player player;
        try {
            player = gameRepository.join(request.getRemoteAddr(), id);
            log.info("Player joined: tankId={} IP={}", player.getTank().getId(), request.getRemoteAddr());
            long tankId;
            if (player.getTank() == null) {
                tankId = -1;
            } else {
                tankId = player.getTank().getId();
            }
            long builderId;
            if (player.getBuilder() == null) {
                builderId = -1;
            } else {
                builderId = player.getBuilder().getId();
            }

            return new ResponseEntity<PlayerWrapper>(
                    new PlayerWrapper(tankId, builderId),
                    HttpStatus.CREATED
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<GridWrapper> grid() {
        return new ResponseEntity<GridWrapper>(new GridWrapper(gameRepository.getGrid()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "terrain", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<GridWrapper> terrainGrid() {
        return new ResponseEntity<GridWrapper>(new GridWrapper(gameRepository.getTerrainGrid()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{millis}/event", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<StringArrayWrapper> event(@PathVariable long millis) {
        return new ResponseEntity<StringArrayWrapper>(new StringArrayWrapper(gameRepository.event(millis)), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/turn/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> turn(@PathVariable long tankId, @PathVariable byte direction)
            throws TokenDoesNotExistException, LimitExceededException, IllegalTransitionException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.turn(tankId, Direction.fromByte(direction))),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/move/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<LongWrapper> move(@PathVariable long tankId, @PathVariable byte direction)
            throws TokenDoesNotExistException, LimitExceededException, IllegalTransitionException {
        long result = gameRepository.move(tankId, Direction.fromByte(direction));
        System.out.printf("RESULT: %d", result);
        return new ResponseEntity<LongWrapper>(
                new LongWrapper(result),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/fire", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long tankId)
            throws TokenDoesNotExistException, LimitExceededException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.fire(tankId, 1)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/fire/{bulletType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long tankId, @PathVariable int bulletType)
            throws TokenDoesNotExistException, LimitExceededException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.fire(tankId, bulletType)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/eject", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<LongWrapper> eject(@PathVariable long tankId)
            throws TokenDoesNotExistException {
        Soldier soldier = gameRepository.eject(tankId);
        if (soldier == null) {
            return new ResponseEntity<LongWrapper>(new LongWrapper(-1), HttpStatus.OK);
        }
        return new ResponseEntity<>(new LongWrapper(soldier.getId()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{tankId}/leave", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    HttpStatus leave(@PathVariable long tankId)
            throws TokenDoesNotExistException {
        //System.out.println("Games Controller leave() called, tank ID: "+tankId);
        gameRepository.leave(tankId);
        return HttpStatus.OK;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{builderId}/build/{improvementType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> build(@PathVariable long builderId, @PathVariable byte improvementType)
            throws TokenDoesNotExistException, LimitExceededException, IllegalTransitionException {
        boolean result = gameRepository.build(builderId, improvementType, false);
        return new ResponseEntity<>(new BooleanWrapper(result), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{builderId}/dismantle", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> dismantle(@PathVariable long builderId)
            throws TokenDoesNotExistException, LimitExceededException, IllegalTransitionException {
        boolean result = gameRepository.dismantle(builderId, false);
        return new ResponseEntity<>(new BooleanWrapper(result), HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleBadRequests(Exception e) {
        return e.getMessage();
    }



    //return inventory to rest client call

    @RequestMapping(method=RequestMethod.PUT, value="GetInventory/{id}/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody

    ResponseEntity<InventoryWrapper> getInventory(@PathVariable int id)
    {

        try
        {
            int[] inventory = gameRepository.getInventory(id);

            //boolean isPoweredUp =
            return new ResponseEntity<InventoryWrapper>(new InventoryWrapper(inventory), HttpStatus.OK);


        } catch (RestClientException e) {
            e.printStackTrace();
        }

        return null;


    }

    @GetMapping("/tank/health/{tankId}")
    public ResponseEntity<Integer> getTankHealth(@PathVariable long tankId) {
        try {
            int health = gameRepository.getTankHealth(tankId);
            return new ResponseEntity<>(health, HttpStatus.OK);
        } catch (TokenDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/tank/shieldHealth/{tankId}")
    public ResponseEntity<Integer> getTankShieldHealth(@PathVariable long tankId) {
        try {
            int shieldHealth = gameRepository.getTankShieldHealth(tankId);
            return new ResponseEntity<>(shieldHealth, HttpStatus.OK);
        } catch (TokenDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/soldier/health/{soldierId}")
    public ResponseEntity<Integer> getSoldierHealth(@PathVariable long soldierId) {
        try {
            int health = gameRepository.getSoldierHealth(soldierId);
            return new ResponseEntity<>(health, HttpStatus.OK);
        } catch (TokenDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/builder/health/{builderId}")
    public ResponseEntity<Integer> getBuilderHealth(@PathVariable long builderId) {
        try {
            int health = gameRepository.getBuilderHealth(builderId);
            return new ResponseEntity<>(health, HttpStatus.OK);
        } catch (TokenDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
