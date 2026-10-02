-- =====================================================================
-- StreetConnect Realistic Sample Seed Data
-- =====================================================================

USE streetconnect_db;

-- 1. Insert Categories
INSERT INTO categories (id, name, description, icon, is_active) VALUES
(1, 'Street Light', 'Issues related to non-functional or flickering street lamps', 'bi-lightbulb', TRUE),
(2, 'Garbage & Waste', 'Overflowing dustbins, uncollected waste, illegal dumping', 'bi-trash', TRUE),
(3, 'Road & Pavement', 'Potholes, damaged tar, missing paver blocks, unsafe walkways', 'bi-signpost-split', TRUE),
(4, 'Drainage & Sewage', 'Clogged storm drains, overflowing sewage, foul smell', 'bi-droplet-half', TRUE),
(5, 'Water Supply', 'Low pressure, broken pipeline, contaminated drinking water', 'bi-water', TRUE),
(6, 'Electricity & Wiring', 'Hanging live wires, transformer sparking, power pole lean', 'bi-lightning-charge', TRUE),
(7, 'Public Safety', 'Lack of surveillance, stray dogs pack, unauthorized blockades', 'bi-shield-exclamation', TRUE),
(8, 'Sanitation & Cleaning', 'Unswept streets, stagnant puddles, mosquito breeding spots', 'bi-recycle', TRUE),
(9, 'Traffic & Parking', 'Illegal parking blocking access, missing speed breakers', 'bi-cone-striped', TRUE),
(10, 'Other Local Issues', 'Park maintenance, tree pruning, general civic queries', 'bi-three-dots', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 2. Insert Streets
INSERT INTO streets (id, name, area, city, pincode, state) VALUES
(1, 'Gandhi Street', 'Tambaram', 'Chennai', '600045', 'Tamil Nadu'),
(2, 'Bharathi Nagar Main Road', 'Velachery', 'Chennai', '600042', 'Tamil Nadu'),
(3, 'Anna Salai 3rd Cross', 'Guindy', 'Chennai', '600032', 'Tamil Nadu')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. Insert Users (Password is 'password123' for residents/moderators, 'admin123' for admin)
-- BCrypt encoded hash for 'password123': $2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2
-- BCrypt encoded hash for 'admin123':    $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
INSERT INTO users (id, full_name, email, mobile_number, password, role, street_id, profile_image, is_active) VALUES
-- Admin
(1, 'Suresh Kumar (Admin)', 'admin@streetconnect.com', '9840112233', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_ADMIN', 1, 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80', TRUE),

-- Street Moderators
(2, 'Venkatesh Raman', 'mod.tambaram@streetconnect.com', '9840223344', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_MODERATOR', 1, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80', TRUE),
(3, 'Meenakshi Sundaram', 'mod.velachery@streetconnect.com', '9840334455', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_MODERATOR', 2, 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=150&q=80', TRUE),

-- Residents - Gandhi Street (Street 1)
(4, 'Arun Prakash', 'arun.prakash@gmail.com', '9840445566', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 1, 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80', TRUE),
(5, 'Priya Krishnan', 'priya.k@gmail.com', '9840556677', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 1, 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80', TRUE),
(6, 'Karthik Raja', 'karthik.raja@yahoo.com', '9840667788', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 1, 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=150&q=80', TRUE),
(7, 'Deepa Balaji', 'deepa.balaji@hotmail.com', '9840778899', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 1, 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80', TRUE),

-- Residents - Bharathi Nagar (Street 2)
(8, 'Rajeshwari S', 'rajeshwari.s@gmail.com', '9840889900', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 2, 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=150&q=80', TRUE),
(9, 'Siddharth M', 'sid.murugan@outlook.com', '9840990011', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 2, 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=150&q=80', TRUE),
(10, 'Ananya Ram', 'ananya.ram@gmail.com', '9840001122', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 2, 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80', TRUE),

-- Residents - Anna Salai 3rd Cross (Street 3)
(11, 'Dinesh Chandran', 'dinesh.c@gmail.com', '9840123450', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 3, 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=150&q=80', TRUE),
(12, 'Lakshmi Narayanan', 'lakshmi.n@gmail.com', '9840234561', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 3, 'https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&w=150&q=80', TRUE),
(13, 'Saravanan K', 'saravanan.k@gmail.com', '9840345672', '$2a$10$K87PqP/61sJpfnwM/8Fh1e9j2kI3N0HqU8fS7o6g4Vv2w1xY0z5y2', 'ROLE_RESIDENT', 3, 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&q=80', TRUE)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 4. Insert Complaints (15 Realistic Complaints)
INSERT INTO complaints (id, title, description, category_id, street_id, user_id, landmark, image_url, priority, status, official_remark, resolution_notes, contact_number, created_at, updated_at, resolved_at) VALUES
(1, 'Street Light Not Working Near Corner House', 'The LED street light pole #GT-14 has been dark for past 4 days. Women and senior citizens returning late face severe safety hazard due to pitch black road.', 1, 1, 4, 'Opposite House No. 24, Near Vinayagar Temple', 'https://images.unsplash.com/photo-1517420879524-86d64ac2f339?auto=format&fit=crop&w=800&q=80', 'HIGH', 'UNDER_REVIEW', 'Inspection assigned to Junior Engineer EB Tambaram ward.', NULL, '9840445566', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY, NULL),

(2, 'Garbage Overflowing at Street Entrance Bin', 'Municipal garbage dump bin has not been cleared since Saturday morning. Stray animals are scattering waste all over the road causing intolerable stench.', 2, 1, 5, 'Near Gandhi Street Entrance Arch', 'https://images.unsplash.com/photo-1530587191325-3db32d826c18?auto=format&fit=crop&w=800&q=80', 'CRITICAL', 'IN_PROGRESS', 'Conservancy supervisor notified. Special collection vehicle dispatched today afternoon.', NULL, '9840556677', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 3 HOUR, NULL),

(3, 'Dangerous Deep Pothole After Rainwater Pipe Repair', 'Contractors left a 2-foot deep trench unpaved after metro water connection. Two two-wheelers skid and fell yesterday night.', 3, 1, 6, 'In front of Green Enclave Apartments Gate 2', 'https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?auto=format&fit=crop&w=800&q=80', 'HIGH', 'PENDING', NULL, NULL, '9840667788', NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, NULL),

(4, 'Drainage Chamber Overflowing with Foul Odor', 'Drainage manhole cover is blocked with silt and sewage is spilling onto pedestrian walkway near school gate.', 4, 1, 7, 'Adjacent to Gandhi High School Back Gate', 'https://images.unsplash.com/photo-1584824486509-112e4181ff6b?auto=format&fit=crop&w=800&q=80', 'CRITICAL', 'RESOLVED', 'Super-sucker suction vehicle deployed by CMWSSB. Blockage cleared thoroughly.', 'Silt removed and concrete slab re-sealed properly.', '9840778899', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),

(5, 'Drinking Water Supply Discolored and Smelly', 'Tap water received today at 6 AM was muddy and yellowish with chemical odor. Kindly inspect main line pipeline leak.', 5, 1, 4, 'Gandhi Street Cross Cut 1', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=800&q=80', 'HIGH', 'UNDER_REVIEW', 'Water sample collected for quality test at Zonal Lab.', NULL, '9840445566', NOW() - INTERVAL 18 HOUR, NOW() - INTERVAL 10 HOUR, NULL),

(6, 'Low Hanging Electric Cable Across Roadway', 'Fiber optic and electrical cable bundle sagging below 7 feet height. Delivery trucks and school vans risk snapping it.', 6, 1, 5, 'Near Pole #8, Mid Street', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?auto=format&fit=crop&w=800&q=80', 'CRITICAL', 'RESOLVED', 'Cable operators and TANGEDCO lineworker bundled and hoisted wires to safe 16-ft bracket.', 'Clearance certified safe for heavy vehicles.', '9840556677', NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY),

(7, 'Pack of Aggressive Stray Dogs Near Park Area', 'Over 7 stray dogs barking aggressively at cyclists and school kids every evening around 6 PM.', 7, 1, 6, 'Near Community Park Walking Track', 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=800&q=80', 'MEDIUM', 'PENDING', NULL, NULL, '9840667788', NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 12 HOUR, NULL),

(8, 'Storm Water Drain Blocked with Construction Debris', 'Individual building construction at Plot 12 dumped sand into stormwater canal, blocking monsoon flow.', 8, 1, 7, 'Opposite Plot 12, Gandhi Street', 'https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=800&q=80', 'HIGH', 'IN_PROGRESS', 'Show cause notice issued to plot owner. Clearing underway with JCB excavator.', NULL, '9840778899', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 1 DAY, NULL),

-- Street 2: Bharathi Nagar
(9, 'Broken Manhole Cover Threatening Motorists', 'Heavy vehicle broke the concrete manhole cover. Locals placed a tree branch as warning marker.', 3, 2, 8, 'Bharathi Nagar Junction, Near Nilgiris Supermarket', 'https://images.unsplash.com/photo-1541888946425-d0fbb186c5f7?auto=format&fit=crop&w=800&q=80', 'CRITICAL', 'IN_PROGRESS', 'New ductile iron heavy-duty cover ordered from corporation stockyard.', NULL, '9840889900', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 5 HOUR, NULL),

(10, 'Street Sweeping Missing for Past 10 Days', 'Dry leaves, plastic wrappers, and roadside dust piled up along both sides of Bharathi Nagar 2nd lane.', 8, 2, 9, 'Bharathi Nagar 2nd Lane', 'https://images.unsplash.com/photo-1618477388954-7852f32655ec?auto=format&fit=crop&w=800&q=80', 'MEDIUM', 'RESOLVED', 'Sanitary inspector conducted roll call inspection. Dedicated morning sweeper assigned.', 'Cleaned on Oct 1. Daily roster updated.', '9840990011', NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),

(11, 'Illegal Commercial Vehicle Parking Blocking Fire Engine Lane', 'Private tourist buses parking permanently on both sides leaving zero room for ambulances or garbage trucks.', 9, 2, 10, 'End of Bharathi Nagar Main Road', 'https://images.unsplash.com/photo-1506521781263-d8422e82f27a?auto=format&fit=crop&w=800&q=80', 'HIGH', 'UNDER_REVIEW', 'Traffic police inspector Velachery station notified for towing drive.', NULL, '9840001122', NOW() - INTERVAL 36 HOUR, NOW() - INTERVAL 12 HOUR, NULL),

(12, 'Community Park Light Bulb Stolen / Broken', 'Two solar streetlights installed in the children park are vandalized and not turning on at dusk.', 1, 2, 8, 'Bharathi Nagar Children Playground', 'https://images.unsplash.com/photo-1517420879524-86d64ac2f339?auto=format&fit=crop&w=800&q=80', 'LOW', 'PENDING', NULL, NULL, '9840889900', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL),

-- Street 3: Anna Salai 3rd Cross
(13, 'Open Transformer Gate Left Unlocked', 'Substation distribution transformer gate lock is broken, children play cricket right next to high voltage transformer.', 6, 3, 11, 'Opposite Post Office, Anna Salai 3rd Cross', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?auto=format&fit=crop&w=800&q=80', 'CRITICAL', 'RESOLVED', 'Urgent TNEB emergency team sent within 2 hours. Heavy padlock installed and warning sign repainted.', 'Secured and verified safe.', '9840123450', NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),

(14, 'Dead Tree Branch Leaning Dangerously on Cable Lines', 'Huge banyan branch cracked during stormy gust and hanging precarious over pedestrian path.', 10, 3, 12, 'Near House #10B', 'https://images.unsplash.com/photo-1542273917363-3b1817f69a2d?auto=format&fit=crop&w=800&q=80', 'HIGH', 'IN_PROGRESS', 'Parks department tree-cutting crew scheduled with hydraulic lift for tomorrow 8 AM.', NULL, '9840234561', NOW() - INTERVAL 16 HOUR, NOW() - INTERVAL 4 HOUR, NULL),

(15, 'Invalid / Non-Civic Complaint (Spam)', 'Requesting to change the paint of private neighboring villa because I dislike the color yellow.', 10, 3, 13, 'Private compound', NULL, 'LOW', 'REJECTED', 'StreetConnect handles public infrastructure and community issues only. Private property disputes are outside platform purview.', NULL, '9840345672', NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 6 DAY, NULL)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 5. Insert Status Audit History
INSERT INTO complaint_status_history (complaint_id, previous_status, new_status, changed_by_user_id, remark, created_at) VALUES
(1, 'PENDING', 'UNDER_REVIEW', 2, 'Field inspection ordered with ward electrical engineer.', NOW() - INTERVAL 1 DAY),
(2, 'PENDING', 'UNDER_REVIEW', 2, 'Contacted sanitary inspector regarding missed schedule.', NOW() - INTERVAL 18 HOUR),
(2, 'UNDER_REVIEW', 'IN_PROGRESS', 2, 'Waste clearing truck dispatched with 4 workers.', NOW() - INTERVAL 3 HOUR),
(4, 'PENDING', 'UNDER_REVIEW', 2, 'Drainage team notified of overflowing blackwater.', NOW() - INTERVAL 4 DAY),
(4, 'UNDER_REVIEW', 'IN_PROGRESS', 2, 'Suction tanker truck arrived on site.', NOW() - INTERVAL 3 DAY),
(4, 'IN_PROGRESS', 'RESOLVED', 2, 'Drain cleared, disinfected with bleaching powder. Verified clear.', NOW() - INTERVAL 2 DAY),
(6, 'PENDING', 'UNDER_REVIEW', 2, 'High priority safety hazard flagged to TNEB.', NOW() - INTERVAL 7 DAY),
(6, 'UNDER_REVIEW', 'IN_PROGRESS', 2, 'Lineman on site tightening overhead cables.', NOW() - INTERVAL 5 DAY),
(6, 'IN_PROGRESS', 'RESOLVED', 2, 'Overhead clearance confirmed safe at 16 ft.', NOW() - INTERVAL 4 DAY),
(9, 'PENDING', 'IN_PROGRESS', 3, 'Replacement iron cover ordered for manhole.', NOW() - INTERVAL 5 HOUR),
(10, 'PENDING', 'IN_PROGRESS', 3, 'Sanitary gang assigned to sweep the entire lane.', NOW() - INTERVAL 2 DAY),
(10, 'IN_PROGRESS', 'RESOLVED', 3, 'Cleaned thoroughly and photographed by inspector.', NOW() - INTERVAL 1 DAY),
(13, 'PENDING', 'RESOLVED', 1, 'Emergency response: High voltage transformer cage locked and secured.', NOW() - INTERVAL 3 DAY),
(15, 'PENDING', 'REJECTED', 1, 'Private property painting dispute not eligible under civic complaint guidelines.', NOW() - INTERVAL 6 DAY);

-- 6. Insert Comments
INSERT INTO comments (complaint_id, user_id, content, created_at) VALUES
(1, 5, 'Yes! I was walking back from bus stand yesterday and had to turn on mobile flashlight. Very dangerous.', NOW() - INTERVAL 1 DAY),
(1, 6, 'Same problem near my house at pole #12 too. Thanks Arun for raising this promptly!', NOW() - INTERVAL 20 HOUR),
(1, 2, 'Official Update: Assistant Engineer has confirmed replacement bulb will be fitted by tomorrow evening.', NOW() - INTERVAL 12 HOUR),
(2, 4, 'The smell is coming all the way to 3rd cross road. Glad the truck is arriving.', NOW() - INTERVAL 2 HOUR),
(2, 6, 'Please also request the municipality to place a bigger 240L wheelie bin here.', NOW() - INTERVAL 1 HOUR),
(3, 5, 'My husband almost lost balance on scooter this morning! Very urgent fix needed.', NOW() - INTERVAL 4 HOUR),
(4, 4, 'Thank you Venkatesh sir and CMWSSB team for fixing this within 48 hours!', NOW() - INTERVAL 1 DAY),
(9, 9, 'I placed that branch yesterday so night bikes won’t fall in. Please hurry with the new cover.', NOW() - INTERVAL 4 HOUR);

-- 7. Insert Supports / Upvotes
INSERT INTO supports (complaint_id, user_id, created_at) VALUES
(1, 4, NOW() - INTERVAL 2 DAY),
(1, 5, NOW() - INTERVAL 2 DAY),
(1, 6, NOW() - INTERVAL 1 DAY),
(1, 7, NOW() - INTERVAL 1 DAY),
(2, 4, NOW() - INTERVAL 1 DAY),
(2, 5, NOW() - INTERVAL 1 DAY),
(2, 6, NOW() - INTERVAL 18 HOUR),
(2, 7, NOW() - INTERVAL 12 HOUR),
(3, 4, NOW() - INTERVAL 5 HOUR),
(3, 5, NOW() - INTERVAL 4 HOUR),
(3, 6, NOW() - INTERVAL 3 HOUR),
(4, 4, NOW() - INTERVAL 4 DAY),
(4, 5, NOW() - INTERVAL 4 DAY),
(4, 6, NOW() - INTERVAL 3 DAY),
(4, 7, NOW() - INTERVAL 3 DAY),
(9, 8, NOW() - INTERVAL 1 DAY),
(9, 9, NOW() - INTERVAL 18 HOUR),
(9, 10, NOW() - INTERVAL 12 HOUR),
(13, 11, NOW() - INTERVAL 4 DAY),
(13, 12, NOW() - INTERVAL 4 DAY),
(13, 13, NOW() - INTERVAL 3 DAY);

-- 8. Insert Notifications
INSERT INTO notifications (user_id, title, message, link, type, is_read, created_at) VALUES
(4, 'Complaint Status Updated', 'Your complaint "Street Light Not Working Near Corner House" is now Under Review by Moderator Venkatesh.', '/complaint-detail.html?id=1', 'STATUS_UPDATE', FALSE, NOW() - INTERVAL 1 DAY),
(4, 'New Comment on Your Issue', 'Priya Krishnan commented: "Yes! I was walking back from bus stand yesterday..."', '/complaint-detail.html?id=1', 'COMMENT', FALSE, NOW() - INTERVAL 22 HOUR),
(4, 'New Support Received', 'Karthik Raja supported your complaint "Street Light Not Working Near Corner House".', '/complaint-detail.html?id=1', 'SUPPORT', TRUE, NOW() - INTERVAL 18 HOUR),
(5, 'Complaint Status Updated', 'Your complaint "Garbage Overflowing at Street Entrance Bin" has moved to In Progress.', '/complaint-detail.html?id=2', 'STATUS_UPDATE', FALSE, NOW() - INTERVAL 3 HOUR),
(7, 'Issue Resolved!', 'Your reported drainage complaint has been marked as RESOLVED by Corporation team.', '/complaint-detail.html?id=4', 'STATUS_UPDATE', TRUE, NOW() - INTERVAL 2 DAY),
(4, 'Community Announcement', 'Street cleaning drive and dengue fogging scheduled for this Saturday morning 7 AM.', '/community.html', 'ANNOUNCEMENT', FALSE, NOW() - INTERVAL 1 DAY);

-- 9. Insert Announcements
INSERT INTO announcements (street_id, created_by_user_id, title, content, priority, is_active, created_at) VALUES
(1, 2, 'Weekly Street Cleaning & Fogging Drive', 'Tambaram municipality health squad will conduct mosquito fogging and street spraying this Saturday, 7:00 AM to 9:30 AM. Kindly keep front gates unlocked.', 'IMPORTANT', TRUE, NOW() - INTERVAL 2 DAY),
(1, 2, 'Water Pipeline Maintenance Notice', 'Metro water valve replacement scheduled for Thursday. Water pressure might be low between 1 PM and 5 PM.', 'NORMAL', TRUE, NOW() - INTERVAL 1 DAY),
(NULL, 1, 'Welcome to StreetConnect Community Platform', 'We are thrilled to launch StreetConnect for our neighborhoods. Post civic concerns, support neighbors, and follow official resolutions directly!', 'NORMAL', TRUE, NOW() - INTERVAL 5 DAY);
