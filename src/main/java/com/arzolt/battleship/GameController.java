package com.arzolt.battleship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin
public class GameController {
	
	//Temporary ?
	private Map<Long,Game> games = new HashMap<Long, Game>();
	//
	
    private final AtomicLong counter = new AtomicLong();

    
    //Create a new game
    @RequestMapping(value="/games", method= RequestMethod.POST, consumes = "application/JSON")
    @ResponseBody
    public ResponseEntity<Game> postGame(@RequestBody Player player) {
    	Game game = new Game(counter.incrementAndGet(),player);
    	games.put(game.getId(), game);    	
    	return new ResponseEntity<Game>(game,HttpStatus.CREATED);
    }
    
    //return games with only one player
    @RequestMapping(value="/games", method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Game>> getGames() {
    	if(this.games.isEmpty()) {
    		return new ResponseEntity<>(null,HttpStatus.OK);
    	} else {
	    	List<Game> games = new ArrayList<>(this.games.values());
	    	List<Game> openGames = new ArrayList<Game>();
	    	for (Game game : games) {
				if(game.getP2()==null) {
					openGames.add(game);
				}
			}
	    	return new ResponseEntity<List<Game>>(openGames,HttpStatus.OK);
    	}
    }
    
    //register a second player
    @RequestMapping(value="/games/{id}", method= RequestMethod.PUT, consumes = "application/JSON")
    @ResponseBody
    public ResponseEntity<Game> putGame(@PathVariable(value="id") String id,
			@RequestBody Player player) {
    	
    	ResponseEntity<Game> result;
    	Game game = games.get(Long.valueOf(id));
    	//ensure that 2 players can't register at the same time
    	synchronized (game) {
			if(game.getP2()==null) {
				game.setP2(player);
				result = new ResponseEntity<>(game,HttpStatus.CREATED);
			} else {
				result = new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			game.notifyAll();
		}
    	return result;
    }
    
    //register player's fleet
    @RequestMapping(value="/games/{id}/fleet")
    @ResponseBody
    public ResponseEntity<Game> putFleet(@PathVariable(value="id") String id,
    									@RequestBody fleetMessageBody body){
    	ResponseEntity<Game> response;
    	Game game = games.get(Long.valueOf(id));
    	if(body.getPlayer().getName().equals(game.getP1().getName())) {
    		game.setFleet1(body.getFleet());
    		response = new ResponseEntity<>(game,HttpStatus.CREATED);    			
    	} else if(body.getPlayer().getName().equals(game.getP2().getName())) {
    		game.setFleet2(body.getFleet());
    		response =  new ResponseEntity<>(game,HttpStatus.CREATED);
    	} else {
    		response =  new ResponseEntity<>(HttpStatus.FORBIDDEN);    		
    	}
    	synchronized(game) {
    		game.notifyAll();    		
    	}
		return response;

    }
    
    //Hang until its player turn (long poll)
    @RequestMapping(value="/games/{id}/turn")
    @ResponseBody
    public ResponseEntity<Boolean> getTurn(@PathVariable(value="id") String id,
    								@RequestParam String player){
    	  	
    	Game game = games.get(Long.valueOf(id));
    	
    	synchronized (game) {
    		while(!game.placementFinished()) {
    			System.out.println(player+" in first loop");


	    		try {
					game.wait();
				} catch (InterruptedException e) {
					return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
    		}
			System.out.println(player+" out of second loop");

			while(!game.playerTurn().getName().equals(player)) {
    			System.out.println(player+" in second loop");
    			System.out.println(game.playerTurn().getName()+" turn to play");

	    		try {
					game.wait();
				} catch (InterruptedException e) {
					return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
    		}
			System.out.println(player+" out of second loop");

    	}
    	System.out.println("sending True to "+player);
		return new ResponseEntity<Boolean>(true,HttpStatus.OK);
    }
    
    
    //return hit from last opponent's play
    @RequestMapping(value="/games/{id}/hit", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Hit> getLastHit(@PathVariable(value="id") String id,
    		@RequestParam String player){
		
    	ResponseEntity<Hit> response;
    	
    	Game game = games.get(Long.valueOf(id));
    	
    	if(player.equals(game.getP1().getName())){
    		if(game.getHitsP2().size()>0) {
	    		response = new ResponseEntity<Hit>(
	    				game.getHitsP2().get(game.getHitsP2().size()-1),HttpStatus.OK);
    		} else {
    			response = new ResponseEntity<>(
	    				null,HttpStatus.OK);
    		}
    		
    	} else if (player.equals(game.getP2().getName())) {
    		if(game.getHitsP1().size()>0) {
	    		response = new ResponseEntity<Hit>(
	    				game.getHitsP1().get(game.getHitsP1().size()-1),HttpStatus.OK);
    		} else {
    			response = new ResponseEntity<>(
	    				null,HttpStatus.OK);
    		}
    	} else {
    		response = new ResponseEntity<>(HttpStatus.FORBIDDEN);
    	}
    	
    	return response;

    }
    
    @RequestMapping(value="/games/{id}/hit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Hit> postHit(@PathVariable(value="id") String id,
    								@RequestBody HitMessageBody body){
	
    	Game game = games.get(Long.valueOf(id));
    	Hit newHit = new Hit();
    	newHit.setTarget(body.getPosition());

    	if(body.getPlayer().equals(game.getP1())){
    		game.getHitsP1().add(newHit);
    		
    	} else if (body.getPlayer().equals(game.getP2())) {
    		game.getHitsP2().add(newHit);

    	} else {
    		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    	}
  	
    	Ship shipHit = game.shipDamaged(body.getPosition(), body.getPlayer());
    	if(shipHit != null){
    		if(game.isShipSunk(shipHit)) {
    			newHit.setResult(Hit.Result.SINK);
    			newHit.setSunked(shipHit);
    		} else {
    			newHit.setResult(Hit.Result.HIT);
    		}
    	} else {
    		newHit.setResult(Hit.Result.MISS);
    	}
    	
    	synchronized (game) {
			game.notifyAll();
		}
    	
    	return new ResponseEntity<Hit>(newHit,HttpStatus.CREATED);
    }
 
}
