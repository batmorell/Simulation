package queueService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class AdministrationBuilder implements ContextBuilder<Object> {
	
	private Context<Object> context;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	@Override
	public Context build(Context<Object> context) {
		this.context = context;
		
		context.setId("queueService");
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		
		this.space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.StrictBorders(),
				50, 50);
					
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		
		this.grid = gridFactory.createGrid("grid", context, 
				new GridBuilderParameters<Object>(new StrictBorders(),
						new SimpleGridAdder<Object>(),
						true, 50, 50));
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		int userCount = (Integer)params.getValue("user_count");
		
		for(int i = 0; i < userCount; i++) {
			User user = new User(space, grid, ThreadLocalRandom.current().nextInt(10, 20 + 1), 0);
			context.add(user);
			Administration.waitingQueue.add(user);
		}
		
		context.add(new Administration(space, grid));
		
		Guichet aGuichet = new Guichet(space, grid);

		context.add(aGuichet);
		
		aGuichet.space.moveTo(aGuichet, 25,30);
		aGuichet.grid.moveTo(aGuichet, 25,30);
		
		Administration.listOfGuichet.add(aGuichet);

		return context;
	}
	
}
