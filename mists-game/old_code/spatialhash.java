private void generateSpatialHash() {
	//---Generate the spatial hash with tilesize of 10*10 ---
        //TODO: is 20 good size?
        int spatialHashWidth = (int)(this.map.getWidth()/spatialHashSize);
        if ((this.map.getWidth()%spatialHashSize) > 0) spatialHashWidth++;
        int spatialHashHeight = (int)(this.map.getHeight()/spatialHashSize);
        if ((this.map.getHeight()%spatialHashSize) > 0) spatialHashHeight++;
        this.spatialHash = new HashMap<>();
        for (int i = 0; i < spatialHashWidth*spatialHashHeight; i++)
        {
            spatialHash.put(i, new ArrayList());
        }
        
}

private void clearSpatialHash() {
        for (int i : this.spatialHash.keySet()) {
            this.spatialHash.get(i).clear();
        }
    }
    
    private void refreshSpatialHash() {
        this.clearSpatialHash();
        this.addAllMobsToSpatialHash();
    }
    
    private void addAllMobsToSpatialHash() {
        for (MapObject s : this.structures) {
            List<Integer> sl = this.getSpatialHashBuckets(s);
            for (Integer bucketId : sl) {
                if (this.spatialHash.get(bucketId) != null)
                this.spatialHash.get(bucketId).add(s);
            }
        }
        for (MapObject c : this.creatures) {
            ArrayList<Integer> cl = this.getSpatialHashBuckets(c);
            for (Integer bucketId : cl) {
                if (this.spatialHash.get(bucketId) != null)
                this.spatialHash.get(bucketId).add(c);
            }
        }
    }
    
    private ArrayList<MapObject> getNearby(MapObject mob)
    {
        ArrayList<MapObject> objects = new ArrayList();
        List<Integer> bucketIds = getSpatialHashBuckets(mob);
        for (Integer bucketId : bucketIds) {
            if (this.spatialHash.get(bucketId) != null)
            objects.addAll(this.spatialHash.get(bucketId));
        }
        objects.remove(mob);
        return objects;   
    }
    
    private void addToSpatialHash(double[] position, double width, List buckettoaddto)
    {  
        int cellPosition = (int)(
                   (Math.floor(position[0] / spatialHashSize)) +
                   (Math.floor(position[1] / spatialHashSize)) *
                   width   
        );
        if(!buckettoaddto.contains(cellPosition))
            buckettoaddto.add(cellPosition);
            
    }
    
    private ArrayList getSpatialHashBuckets(MapObject mob)
    {
        ArrayList bucketsObjIsIn = new ArrayList();
           
        double[] min = new double[]{
            mob.getCenterXPos() - (mob.getSprite().getWidth()/2),
            mob.getCenterYPos() - (mob.getSprite().getHeight()/2)};   
        double[] max = new double[]{
            mob.getCenterXPos() + (mob.getSprite().getWidth()/2),
            mob.getCenterYPos() + (mob.getSprite().getHeight()/2)};   

        double width = this.map.getWidth() / spatialHashSize;   
        //TopLeft
        addToSpatialHash(min,width,bucketsObjIsIn);
        //TopRight
        addToSpatialHash(new double[]{max[0], min[1]}, width, bucketsObjIsIn);
        //BottomRight
        addToSpatialHash(new double[]{max[0], max[1]}, width, bucketsObjIsIn);
        //BottomLeft
        addToSpatialHash(new double[]{min[0], max[1]}, width, bucketsObjIsIn);

	return bucketsObjIsIn;    
    }