<?
	include_once("config.php");
	
?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
		<link rel="stylesheet" href="style/style.css" />
		<link rel="stylesheet" href="style/gradients.css" />
		<link rel="stylesheet" href="jquery-ui-1.10.3/css/smoothness/jquery-ui.css" >
		
		<link rel="shortcut icon" href="ressources/favicon.ico"/>
		
		<script src="jquery-ui-1.10.3/js/jquery-1.9.1.js"></script>
		<script src="jquery-ui-1.10.3/js/jquery-ui-1.10.3.custom.js"></script>
		
		<!-- DataTables CSS -->
		<link rel="stylesheet" type="text/css" href="DataTables-1.10.2/media/css/jquery.dataTables.css">
		<link rel="stylesheet" type="text/css" href="DataTables-1.10.2/extensions/ColVis/css/dataTables.colVis.css">
		  
		<!-- DataTables -->
		<script type="text/javascript" charset="utf8" src="DataTables-1.10.2/media/js/jquery.dataTables.js"></script>
		<script type="text/javascript" charset="utf8" src="DataTables-1.10.2/extensions/ColVis/js/dataTables.colVis.js"></script>
		
        <title>DJMusic Logs</title>
    </head>
 
    <body>
		
		<div style="-moz-border-radius: 15px;border-radius: 15px;border:1px black solid;padding:10px;margin-top:10px">
		<h3>
			LOGS
		</h3>
		
		<script type="text/javascript">
			$(document).ready( function () {
				$('#logs_display').DataTable( {
					"dom": 'C<"clear">lfrtip',
					"language" : 
					{
						"sProcessing":     "Traitement en cours...",
						"sSearch":         "Rechercher&nbsp;:",
						"sLengthMenu":     "Afficher _MENU_ logs",
						"sInfo":           "Affichage du log _START_ &agrave; _END_ sur _TOTAL_ logs",
						"sInfoEmpty":      "Aucun log",
						"sInfoFiltered":   "(filtr&eacute; de _MAX_ &eacute;l&eacute;ments au total)",
						"sInfoPostFix":    "",
						"sLoadingRecords": "Chargement en cours...",
						"sZeroRecords":    "Aucun log &agrave; afficher",
						"sEmptyTable":     "Aucun log disponible",
						"oPaginate": {
							"sFirst":      "Premier",
							"sPrevious":   "Pr&eacute;c&eacute;dent",
							"sNext":       "Suivant",
							"sLast":       "Dernier"
						},
						"oAria": {
							"sSortAscending":  ": activer pour trier la colonne par ordre croissant",
							"sSortDescending": ": activer pour trier la colonne par ordre décroissant"
						}
					},
					"colVis": {
						"buttonText": "Afficher/Cacher"
					},
					"stateSave": true
				} );
			} );
		</script>
		
		<table id="logs_display" class="display center" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th>Ip adress</th>
					<th>Date</th>
					<th>Status</th>
					<th>Details</th>
				</tr>
			</thead>
			<tbody>
	<?php
			
			//On prépare notre requête
			$request = "SELECT * FROM logs";
			
			//Finalement on exécute la requête
			$db->request($request);
			
			//On traite la requête
			while($log = $db->read())
			{
	?>
				<tr>
					<td><?php echo $log['ip']; ?></td>
					<td>
						<?php 
							$date = new DateTime($log['time']);
							echo $date->format('d.m.Y H:i:s'); 
						?>
					</td>
					<td><?php echo $log['status']; ?></td>
					<td><?php echo $log['message']; ?></td>
				</tr>
	<?php
			}
	?>
			</tbody>
		</table>
		</div>
    
	</body>
</html>
